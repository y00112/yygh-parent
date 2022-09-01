package com.wukong.yygh.orders.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wukong.yygh.enums.OrderStatusEnum;
import com.wukong.yygh.enums.PaymentStatusEnum;
import com.wukong.yygh.model.order.OrderInfo;
import com.wukong.yygh.model.order.PaymentInfo;
import com.wukong.yygh.orders.mapper.OrderInfoMapper;
import com.wukong.yygh.orders.mapper.PaymentMapper;
import com.wukong.yygh.orders.service.OrderInfoService;
import com.wukong.yygh.orders.service.PaymentService;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * Created By WuKong on 2022/8/31 18:52
 **/
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, PaymentInfo> implements PaymentService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Override
    public void savePaymentInfo(OrderInfo order, Integer paymentType) {
        // 1、根据订单id查看是否有交易记录
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",order.getId());
        queryWrapper.eq("payment_type", paymentType);
        Integer count = baseMapper.selectCount(queryWrapper);
        if (count > 0){
            return;
        }
        PaymentInfo paymentInfo = new PaymentInfo();
        // 保存交易记录
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(order.getId());
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setOutTradeNo(order.getOutTradeNo());
        paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus());
        String subject = new DateTime(order.getReserveDate()).toString("yyyy-MM-dd")+"|"+order.getHosname()+"|"+order.getDepname()+"|"+order.getTitle();
        paymentInfo.setSubject(subject);
        paymentInfo.setTotalAmount(order.getAmount());

        baseMapper.insert(paymentInfo);

    }

    @Override
    public void paySuccess(String out_trade_no, Integer status, Map<String, String> resultMap) {
        // 1、更新订单状态
        QueryWrapper<OrderInfo> orderInfoQueryWrapper = new QueryWrapper<>();
        orderInfoQueryWrapper.eq("out_trade_no",out_trade_no);
        OrderInfo orderInfo = orderInfoMapper.selectOne(orderInfoQueryWrapper);
        orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());
        orderInfoMapper.updateById(orderInfo);

        //2 更新支付记录状态
        QueryWrapper<PaymentInfo> wrapperPayment = new QueryWrapper<>();
        wrapperPayment.eq("out_trade_no",out_trade_no);
        PaymentInfo paymentInfo = baseMapper.selectOne(wrapperPayment);
        //设置状态
        paymentInfo.setPaymentStatus(PaymentStatusEnum.PAID.getStatus());
        paymentInfo.setTradeNo(resultMap.get("transaction_id"));
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(resultMap.toString());
        baseMapper.updateById(paymentInfo);
    }
}
