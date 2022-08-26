package com.wukong.yygh.hosp.controller;

import com.wukong.yygh.common.result.ResponseResult;
import com.wukong.yygh.hosp.service.ScheduleService;
import com.wukong.yygh.model.hosp.Schedule;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created By WuKong on 2022/8/25 22:42
 **/
@RestController
@RequestMapping("/admin/hosp/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 根据医院编号 和 科室信息，查询排班规则数据
     */
    @ApiOperation(value = "查询排版规则数据")
    @GetMapping("getScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public ResponseResult getSchedulePage(@PathVariable("page") Integer page,
                                          @PathVariable("limit") Integer limit,
                                          @PathVariable("hoscode") String hoscode,
                                          @PathVariable("depcode") String depcode){
        Map<String,Object> map = scheduleService.getSchedulePage(page,limit,hoscode,depcode);

        return ResponseResult.success().data(map);
    }

    /**
     * 根据医院编号 、科室编号和工作日期，查询排班详细信息
     */
    @ApiOperation(value = "查询排班详细信息")
    @GetMapping("getScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public ResponseResult getScheduleDetail( @PathVariable String hoscode,
                                @PathVariable String depcode,
                                @PathVariable String workDate) {
        List<Schedule> list = scheduleService.getDetailSchedule(hoscode,depcode,workDate);
        return ResponseResult.success().data("list",list);
    }

}
