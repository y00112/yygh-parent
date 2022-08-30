package com.wukong.yygh.hosp.service;

import com.wukong.yygh.model.hosp.Schedule;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * Created By WuKong on 2022/8/20 8:26
 **/
public interface ScheduleService {
    void save(Map<String, Object> stringObjectMap);

    Page<Schedule> getSchedulePage(String hoscode, String page, String limit);

    void removeSchedule(String hosScheduleId);

    Map<String, Object> getSchedulePage(Integer page, Integer limit, String hoscode, String depcode);

    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);

    Map<String, Object> getBookingScheduleRule(Integer page, Integer limit, String hoscode, String depcode);

    Schedule getScheduleInfoByscheduleId(String scheduleId);
}
