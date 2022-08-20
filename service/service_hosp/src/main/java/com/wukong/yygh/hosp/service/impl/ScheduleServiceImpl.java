package com.wukong.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.wukong.yygh.hosp.repository.ScheduleRepository;
import com.wukong.yygh.hosp.service.ScheduleService;
import com.wukong.yygh.model.hosp.Department;
import com.wukong.yygh.model.hosp.Hospital;
import com.wukong.yygh.model.hosp.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * Created By WuKong on 2022/8/20 8:26
 **/
@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

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
}
