package com.wukong.yygh.orders.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wukong.yygh.client.HospFeignClient;
import com.wukong.yygh.client.UserFeignClient;
import com.wukong.yygh.common.exception.MyException;
import com.wukong.yygh.common.result.ResponseResult;
import com.wukong.yygh.enums.OrderStatusEnum;
import com.wukong.yygh.model.order.OrderInfo;
import com.wukong.yygh.model.user.Patient;
import com.wukong.yygh.orders.mapper.OrderInfoMapper;
import com.wukong.yygh.orders.service.OrderInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wukong.yygh.orders.utils.HttpClient;
import com.wukong.yygh.orders.utils.HttpRequestHelper;
import com.wukong.yygh.orders.utils.HttpUtil;
import com.wukong.yygh.rabbit.MQConst;
import com.wukong.yygh.rabbit.RabbitService;
import com.wukong.yygh.vo.hosp.ScheduleOrderVo;
import com.wukong.yygh.vo.msm.MsmVo;
import com.wukong.yygh.vo.order.OrderMqVo;
import com.wukong.yygh.vo.order.OrderQueryVo;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author wukong
 * @since 2022-08-30
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Autowired
    private HospFeignClient hospFeignClient;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private RabbitService rabbitService;

    @Override
    public Long saveOrder(String scheduleId, String patientId) {
        OrderInfo orderInfo = new OrderInfo();
        //1、先根据scheduleId获取医生的排班信息
        ScheduleOrderVo scheduleOrderVo = hospFeignClient.getScheduleOrderVo(scheduleId);

        //2、根据 patientId 获取就诊人信息
        ResponseResult patientInfo = userFeignClient.getPatientInfo(patientId);
        Object patient1 = patientInfo.getData().get("patient");
        String string = JSONObject.toJSONString(patient1);
        Patient patient = JSONObject.parseObject(string,Patient.class);


        //3、根据平台信息调用第三方系统，确认是否还能挂号
        Map<String,Object> paramMap = new HashMap();
        paramMap.put("hoscode",scheduleOrderVo.getHoscode());
        paramMap.put("depcode",scheduleOrderVo.getDepcode());
        paramMap.put("hosScheduleId",scheduleOrderVo.getHosScheduleId());
        paramMap.put("reserveDate",new DateTime(scheduleOrderVo.getReserveDate()).toString("yyyy-MM-dd"));
        paramMap.put("reserveTime", scheduleOrderVo.getReserveTime());
        paramMap.put("amount",scheduleOrderVo.getAmount()); //挂号费用
        paramMap.put("name", patient.getName());
        paramMap.put("certificatesType",patient.getCertificatesType());
        paramMap.put("certificatesNo", patient.getCertificatesNo());
        paramMap.put("sex",patient.getSex());
        paramMap.put("birthdate", patient.getBirthdate());
        paramMap.put("phone",patient.getPhone());
        paramMap.put("isMarry", patient.getIsMarry());
        paramMap.put("provinceCode",patient.getProvinceCode());
        paramMap.put("cityCode", patient.getCityCode());
        paramMap.put("districtCode",patient.getDistrictCode());
        paramMap.put("address",patient.getAddress());
        //联系人
        paramMap.put("contactsName",patient.getContactsName());
        paramMap.put("contactsCertificatesType", patient.getContactsCertificatesType());
        paramMap.put("contactsCertificatesNo",patient.getContactsCertificatesNo());
        paramMap.put("contactsPhone",patient.getContactsPhone());
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp());
        //String sign = HttpRequestHelper.getSign(paramMap, signInfoVo.getSignKey());
        paramMap.put("sign", "");
        JSONObject jsonObject = HttpRequestHelper.sendRequest(paramMap, "http://localhost:9999/order/submitOrder");
        if (null != jsonObject){
            int code = jsonObject.getIntValue("code");
            if(code == 200){ //预约成功可以挂号
                // 3.2 如果返回成功，得到返回其他数据
                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                //预约记录唯一标识（医院预约记录主键）
                String hosRecordId = jsonObject1.getString("hosRecordId");
                //预约序号
                Integer number = jsonObject1.getInteger("number");;
                //取号时间
                String fetchTime = jsonObject1.getString("fetchTime");;
                //取号地址
                String fetchAddress = jsonObject1.getString("fetchAddress");;

                //设置添加数据--排班数据
                BeanUtils.copyProperties(scheduleOrderVo, orderInfo);
                //设置添加数据--就诊人数据
                //订单号
                String outTradeNo = System.currentTimeMillis() + ""+ new Random().nextInt(100);
                orderInfo.setOutTradeNo(outTradeNo);
                orderInfo.setScheduleId(scheduleOrderVo.getHosScheduleId());
                orderInfo.setUserId(patient.getUserId());
                orderInfo.setPatientId(Long.parseLong(patientId));
                orderInfo.setPatientName(patient.getName());
                orderInfo.setPatientPhone(patient.getPhone());
                orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());

                //设置添加数据--医院接口返回数据
                orderInfo.setHosRecordId(hosRecordId);
                orderInfo.setNumber(number);
                orderInfo.setFetchTime(fetchTime);
                orderInfo.setFetchAddress(fetchAddress);

                baseMapper.insert(orderInfo);

                //TODO 5 根据医院返回数据，更新排班数量
                //排班可预约数
                Integer reservedNumber = jsonObject1.getInteger("reservedNumber");
                //排班剩余预约数
                Integer availableNumber = jsonObject1.getInteger("availableNumber");

                //TODO 更新排班信息 RabbitMQ
                // 封装消息
                OrderMqVo orderMqVo = new OrderMqVo();
                orderMqVo.setReservedNumber(reservedNumber);
                orderMqVo.setAvailableNumber(availableNumber);
                orderMqVo.setScheduleId(scheduleId);

                MsmVo msmVo = new MsmVo();
                msmVo.setPhone(patient.getPhone());
                String reserveDate =
                        new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd")
                                + (orderInfo.getReserveTime()==0 ? "上午": "下午");
                Map<String,Object> param = new HashMap<String,Object>(){{
                    put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                    put("amount", orderInfo.getAmount());
                    put("reserveDate", reserveDate);
                    put("name", orderInfo.getPatientName());
                    put("quitTime", new DateTime(orderInfo.getQuitTime()).toString("yyyy-MM-dd HH:mm"));
                }};
                msmVo.setParam(param);

                orderMqVo.setMsmVo(msmVo);
                rabbitService.sendMessage(MQConst.EXCHANGE_DIRECT_ORDER,
                                          MQConst.ROUTING_ORDER,
                                            orderMqVo);


                //7 返回订单号
                return orderInfo.getId();
            }else {
                throw new MyException(20001,"挂号异常");
            }
        }

        // 3.1不能挂号，抛出异常

        // 3.2 可以挂号
        // 第一步：要把上面得到的三部分数据插入到order_info表
        // 第二部：更新排班数据中的剩余预约人数
        // 第三步：给就诊人发送预约成功，短信提醒

        // 返回订单id
        return null;
    }


    //实现列表
    //（条件查询带分页）
    @Override
    public IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo) {
        //orderQueryVo获取条件值
        String name = orderQueryVo.getKeyword(); //医院名称
        Long patientId = orderQueryVo.getPatientId(); //就诊人名称
        String orderStatus = orderQueryVo.getOrderStatus(); //订单状态
        String reserveDate = orderQueryVo.getReserveDate();//安排时间
        String createTimeBegin = orderQueryVo.getCreateTimeBegin();
        String createTimeEnd = orderQueryVo.getCreateTimeEnd();
        //对条件值进行非空判断
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(name)) {
            wrapper.like("hosname",name);
        }
        if(!StringUtils.isEmpty(patientId)) {
            wrapper.eq("patient_id",patientId);
        }
        if(!StringUtils.isEmpty(orderStatus)) {
            wrapper.eq("order_status",orderStatus);
        }
        if(!StringUtils.isEmpty(reserveDate)) {
            wrapper.ge("reserve_date",reserveDate);
        }
        if(!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time",createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time",createTimeEnd);
        }
        //调用mapper的方法
        IPage<OrderInfo> pages = baseMapper.selectPage(pageParam, wrapper);
        //编号变成对应值封装
        pages.getRecords().stream().forEach(item -> {
            this.packOrderInfo(item);
        });
        return pages;
    }


    //实现方法
    @Override
    public OrderInfo getOrderInfo(Long id) {
        OrderInfo orderInfo = baseMapper.selectById(id);
        return this.packOrderInfo(orderInfo);
    }

    private OrderInfo packOrderInfo(OrderInfo orderInfo) {
        orderInfo.getParam().put("orderStatusString", OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
        return orderInfo;
    }
}
