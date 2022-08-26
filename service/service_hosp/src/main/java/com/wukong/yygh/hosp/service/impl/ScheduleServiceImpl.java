package com.wukong.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.wukong.yygh.hosp.repository.ScheduleRepository;
import com.wukong.yygh.hosp.service.HospitalService;
import com.wukong.yygh.hosp.service.ScheduleService;
import com.wukong.yygh.model.hosp.Hospital;
import com.wukong.yygh.model.hosp.Schedule;
import com.wukong.yygh.vo.hosp.BookingScheduleRuleVo;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created By WuKong on 2022/8/20 8:26
 **/
@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private HospitalService hospitalService;


    @Override
    public void save(Map<String, Object> stringObjectMap) {
        String stringJson = JSONObject.toJSONString(stringObjectMap);
        Schedule schedule = JSONObject.parseObject(stringJson, Schedule.class);

        // 1、根据医院编号 和 排版编号 查询排版信息
        Schedule targetSchedule =  scheduleRepository.findByHoscodeAndHosScheduleId(schedule.getHoscode(),schedule.getHosScheduleId());
        // 2、如果有医院信息就做更新操作，没有就做添加操作
        if (null == targetSchedule){
            //0：未上线 1：已上线
            schedule.setStatus(1);
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            scheduleRepository.save(schedule);
        }else {
            // 更新操作
            Integer status = targetSchedule.getStatus();
            schedule.setStatus(status);
            schedule.setIsDeleted(0);
            schedule.setUpdateTime(new Date());
            // 根据 hoscode查询 id
            schedule.setId(targetSchedule.getId());
            scheduleRepository.save(schedule);
        }
    }

    @Override
    public Page<Schedule> getSchedulePage(String hoscode, String page, String limit) {

        // 1、分页
        PageRequest pageable = PageRequest.of(Integer.valueOf(page) - 1, Integer.valueOf(limit));
        // 2、创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("hoscode", ExampleMatcher.GenericPropertyMatcher::exact) // 精确查询
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写
        // 3、分页条件
        Schedule schedule = new Schedule();
        schedule.setHoscode(hoscode);
        // 3、 创建查询实例
        Example<Schedule> scheduleExample = Example.of(schedule,matcher);
        Page<Schedule> schedulePage = scheduleRepository.findAll(scheduleExample,pageable);

        return schedulePage;
    }

    @Override
    public void removeSchedule(String hosScheduleId) {
        scheduleRepository.deleteByHosScheduleId(hosScheduleId);
    }

    @Override
    public Map<String, Object> getSchedulePage(Integer page, Integer limit, String hoscode, String depcode) {
        Map<String,Object> map = new HashMap<String,Object>();
        // 1、获取当前页列表
        /*
        TypedAggregation<?> aggregation:
        String inputCollectionName: 输入类型，与集合名称对应的pojo类的字节码
        Class<O> outputType: 输出类型，聚合之后

        select workDate,sum(reservedNumber),sum(availableNumber), count(*) from 表
        where hoscode = #{hoscode} and depcode = #{depcode}
        group by workDate
        Order by workDate ASC
        limit 0, 2
         */
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),   // 设置聚合的查询条件
                Aggregation.group("workDate").first("workDate").as("workDate")
                .count().as("docCount")
                .sum("reservedNumber").as("reservedNumber")  // 预约数
                .sum("availableNumber").as("availableNumber"),   // 空闲数
                Aggregation.sort(Sort.Direction.ASC,"workDate"),
                Aggregation.skip((page -1) * limit),
                Aggregation.limit(limit)
        );

        AggregationResults<BookingScheduleRuleVo> aggregationResults =
                mongoTemplate.aggregate(aggregation,Schedule.class, BookingScheduleRuleVo.class);

        // 当前页的列表
        List<BookingScheduleRuleVo> bookingScheduleRuleList = aggregationResults.getMappedResults();

        for(BookingScheduleRuleVo bookingScheduleRuleVo : bookingScheduleRuleList){
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            // Joda Time
            DateTime dateTime = new DateTime(workDate);
            int dayOfWeek = dateTime.getDayOfWeek(); // 周几
            bookingScheduleRuleVo.setDayOfWeek(swapDayWeek(dayOfWeek));
        }
        // 2、获取总记录数 total
        Aggregation aggregationTotal = Aggregation.newAggregation(
                Aggregation.match(criteria),   // 设置聚合的查询条件
                Aggregation.group("workDate")
        );
        AggregationResults<BookingScheduleRuleVo> totalResult =
                mongoTemplate.aggregate(aggregationTotal,Schedule.class, BookingScheduleRuleVo.class);
        int total = totalResult.getMappedResults().size();


        // 获取最终返回值
        map.put("bookingScheduleRuleList",bookingScheduleRuleList);
        map.put("total",total);


        //获取医院名称
        Hospital hospital = hospitalService.getHospital(hoscode);
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname",hospital.getHosname());
        map.put("baseMap",baseMap);
        return map;
    }

    @Override
    public List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate) {
       List<Schedule> schedules =
               scheduleRepository.findByHoscodeAndDepcodeAndWorkDate(hoscode,depcode,new DateTime(workDate).toDate());
       return schedules;
    }

    private String swapDayWeek(int dayOfWeek) {
        if (dayOfWeek == 1){
            return "周一";
        }else if (dayOfWeek == 2){
            return "周二";
        }else if (dayOfWeek == 3){
            return "周三";
        }else if (dayOfWeek == 4){
            return "周四";
        }else if (dayOfWeek == 5){
            return "周五";
        }else if (dayOfWeek == 6){
            return "周六";
        }else if (dayOfWeek == 7){
            return "周日";
        }
        return "";
    }
}
