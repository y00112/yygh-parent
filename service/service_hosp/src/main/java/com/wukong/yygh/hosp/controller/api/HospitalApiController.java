package com.wukong.yygh.hosp.controller.api;

import com.wukong.yygh.common.result.ResponseResult;
import com.wukong.yygh.hosp.service.DepartmentService;
import com.wukong.yygh.hosp.service.HospitalService;
import com.wukong.yygh.hosp.service.ScheduleService;
import com.wukong.yygh.model.hosp.Hospital;
import com.wukong.yygh.model.hosp.Schedule;
import com.wukong.yygh.vo.hosp.DepartmentVo;
import com.wukong.yygh.vo.hosp.HospitalQueryVo;
import com.wukong.yygh.vo.hosp.ScheduleOrderVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created By WuKong on 2022/8/26 16:56
 **/
@Api(tags = "用户系统使用的医院接口")
@RestController
@RequestMapping("/api/hosp/hospital")
public class HospitalApiController {

    @Autowired
    private HospitalService hospitalService;


    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 根据hoscode医院编号，查询科室信息
     */
    @ApiOperation(value = "根据hoscode医院编号，查询科室信息")
    @GetMapping("/department/list/{hoscode}")
    public ResponseResult getDepartmentListByHoscode(@PathVariable String hoscode){
        List<DepartmentVo> allDepts = departmentService.getAllDepts(hoscode);
        return ResponseResult.success().data("list",allDepts);
    }

    /**
     * 根据hoscode医院编号，查询医院信息
     */
    @ApiOperation(value = "根据hoscode医院编号，查询医院信息")
    @GetMapping("/hospital/list/{hoscode}")
    public ResponseResult getHospitalListByHoscode(@PathVariable String hoscode){
        Hospital hospital = hospitalService.getHospital(hoscode);
        return ResponseResult.success().data("hospital",hospital);
     }

    /**
     *获取首页医院列表 ：带查询条件
     */
    @ApiOperation(value = "获取首页医院列表")
    @GetMapping("/{page}/{limit}")
    public ResponseResult getHospitalPage(@PathVariable(value = "page") String page,
                                          @PathVariable(value = "limit") String limit,
                                          HospitalQueryVo hospitalQueryVo){
        Page<Hospital> hospitals = hospitalService.selectPage(page, limit, hospitalQueryVo);
        return ResponseResult.success().data("pages",hospitals);
    }

    /**
     *  根据医院名称进行模糊查询
     */
    @ApiOperation(value = "获取首页医院列表")
    @GetMapping("/findByNameLike/{name}")
    public ResponseResult getPageList(@PathVariable String name){
        List<Hospital> hospitalList = hospitalService.findByNameLike(name);
        return ResponseResult.success().data("list",hospitalList);
    }

    /**
     * 查询日期范围内的分页信息
     */
    @ApiOperation(value = "获取可预约排班数据")
    @GetMapping("/auth/getBookingScheduleRule/{page}/{limit}/{hoscode}/{depcode}")
    public ResponseResult getScheduleInfo(@PathVariable(value = "page") Integer page,
                                          @PathVariable(value = "limit") Integer limit,
                                          @PathVariable(value = "hoscode") String hoscode,
                                          @PathVariable(value = "depcode") String depcode){
        Map<String,Object> map = scheduleService.getBookingScheduleRule(page,limit,hoscode,depcode);
        return ResponseResult.success().data(map);
    }

    /**
     * 获取排班数据
     */
    @ApiOperation(value = "获取排班数据")
    @GetMapping("/auth/findScheduleList/{hoscode}/{depcode}/{workDate}")
    public ResponseResult findScheduleList(
            @PathVariable String hoscode,
            @PathVariable String depcode,
            @PathVariable String workDate) {
        List<Schedule> scheduleList = scheduleService.getDetailSchedule(hoscode, depcode, workDate);
        return ResponseResult.success().data("scheduleList",scheduleList);
    }

    /**
     * 根据排班id查询排班信息
     */
    @ApiOperation(value = "获取排班详情")
    @GetMapping("getSchedule/{id}")
    public ResponseResult getScheduleInfoById(@PathVariable(value = "id") String scheduleId){
        Schedule schedule = scheduleService.getScheduleInfoByscheduleId(scheduleId);
        return ResponseResult.success().data("schedule",schedule);
    }

    @ApiOperation(value = "根据排班id获取预约下单数据")
    @GetMapping("inner/getScheduleOrderVo/{scheduleId}")
    public ScheduleOrderVo getScheduleOrderVo(
            @ApiParam(name = "scheduleId", value = "排班id", required = true)
            @PathVariable("scheduleId") String scheduleId) {
        return scheduleService.getScheduleOrderVo(scheduleId);
    }

}
