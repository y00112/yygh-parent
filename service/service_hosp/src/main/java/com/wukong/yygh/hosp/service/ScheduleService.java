package com.wukong.yygh.hosp.service;

import com.wukong.yygh.model.hosp.Schedule;
import org.springframework.data.domain.Page;

import java.util.Map;

/**
 * Created By WuKong on 2022/8/20 8:26
 **/
public interface ScheduleService {
    void save(Map<String, Object> stringObjectMap);

    Page<Schedule> getSchedulePage(String hoscode, String page, String limit);

    void removeSchedule(String hosScheduleId);
}
