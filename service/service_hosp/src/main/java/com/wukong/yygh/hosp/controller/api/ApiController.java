package com.wukong.yygh.hosp.controller.api;

import com.wukong.yygh.common.exception.MyException;
import com.wukong.yygh.common.result.ResponseResult;
import com.wukong.yygh.hosp.service.DepartmentService;
import com.wukong.yygh.hosp.service.HospitalService;
import com.wukong.yygh.hosp.service.HospitalSetService;
import com.wukong.yygh.hosp.service.ScheduleService;
import com.wukong.yygh.hosp.util.HttpRequestHelper;
import com.wukong.yygh.hosp.util.MD5;
import com.wukong.yygh.model.hosp.Department;
import com.wukong.yygh.model.hosp.Hospital;
import com.wukong.yygh.model.hosp.Schedule;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created By WuKong on 2022/8/19 14:59
 * 第三方医院接口
 **/
@Api(tags = "医院管理API接口")
@RestController
@RequestMapping("/api/hosp")
@Slf4j
public class ApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    /**
     * /api/hosp/saveHospital
     * 上传医院信息
     */
    @ApiOperation(value = "上传医院信息")
    @PostMapping("/saveHospital")
    public ResponseResult saveHospital(HttpServletRequest request){

        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(request.getParameterMap());
        // 检验 密钥 sign
        // 医院的密钥
        String hospitalSignkey = (String) stringObjectMap.get("sign");
        // 平台的密钥
        String hospitalSetSignKey = hospitalSetService.getSignKey((String)stringObjectMap.get("hoscode"));

        // 非空校验
        if (!StringUtils.isEmpty(hospitalSetSignKey) && !StringUtils.isEmpty(hospitalSignkey)){
            // 判断密钥是否相等
            if (MD5.encrypt(hospitalSetSignKey).equals(hospitalSignkey)){
                //传输过程中“+”转换为了“ ”，因此我们要转换回来
                String logoData = (String)stringObjectMap.get("logoData");
                logoData = logoData.replaceAll(" ","+");
                stringObjectMap.put("logoData",logoData);
                hospitalService.save(stringObjectMap);
                return ResponseResult.success().code(200);  // TODO 有待完善
            }else {
                throw new MyException();
            }

        }

        return ResponseResult.error().code(500);  // TODO 有待完善



    }

    /**
     * /api/hosp/hospital/show
     * 查询医院信息
     */
    @ApiOperation(value = "查询医院信息")
    @PostMapping("/hospital/show")
    public ResponseResult hospitalShow(HttpServletRequest request) {
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(request.getParameterMap());

        String hoscode = (String) stringObjectMap.get("hoscode");

        // 医院的密钥
        String hospitalSignkey = (String) stringObjectMap.get("sign");
        // 平台的密钥
        String hospitalSetSingKey = hospitalSetService.getSignKey(hoscode);

        // 非空校验
        if (!StringUtils.isEmpty(hospitalSetSingKey) && !StringUtils.isEmpty(hospitalSignkey)) {
            // 判断密钥是否相等
            if (MD5.encrypt(hospitalSetSingKey).equals(hospitalSignkey)) {
                Hospital hospital = hospitalService.getHospital(hoscode);
                return ResponseResult.success().code(200).data("data", hospital);  // TODO 有待完善
            } else {
                throw new MyException();
            }
        }

        return ResponseResult.error().code(500);  // TODO 有待完善
    }

    /**
     * http://localhost:8201//api/hosp/saveDepartment
     * /api/hosp/saveSchedule
     * 上传科室信息
     */
    @PostMapping("/saveDepartment")
    public ResponseResult saveDepartment(HttpServletRequest request){
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(request.getParameterMap());

        String hoscode = (String) stringObjectMap.get("hoscode");

        // 医院的密钥
        String hospitalSignkey = (String) stringObjectMap.get("sign");
        // 平台的密钥
        String hospitalSetSingKey = hospitalSetService.getSignKey(hoscode);

        // 非空校验
        if (!StringUtils.isEmpty(hospitalSetSingKey) && !StringUtils.isEmpty(hospitalSignkey)) {
            // 判断密钥是否相等
            if (MD5.encrypt(hospitalSetSingKey).equals(hospitalSignkey)) {
                departmentService.saveDepartment(stringObjectMap);
                return ResponseResult.success().code(200);  // TODO 有待完善
            } else {
                throw new MyException();
            }
        }

        return ResponseResult.error().code(500);  // TODO 有待完善
    }


    /**
     * /api/hosp/department/list
     * 科室列表
     */
    @PostMapping("/department/list")
    public ResponseResult departmentList(HttpServletRequest request){
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(request.getParameterMap());

        String hoscode = (String) stringObjectMap.get("hoscode");
        String page = (String) stringObjectMap.get("page");
        String limit = (String) stringObjectMap.get("limit");

        // 医院的密钥
        String hospitalSignkey = (String) stringObjectMap.get("sign");
        // 平台的密钥
        String hospitalSetSingKey = hospitalSetService.getSignKey(hoscode);

        // 非空校验
        if (!StringUtils.isEmpty(hospitalSetSingKey) && !StringUtils.isEmpty(hospitalSignkey)) {
            // 判断密钥是否相等
            if (MD5.encrypt(hospitalSetSingKey).equals(hospitalSignkey)) {
                Page<Department> departmentList = departmentService.getDepartmentPage(hoscode,page,limit);
                return ResponseResult.success().code(200).data("departments", departmentList);  // TODO 有待完善
            } else {
                throw new MyException();
            }
        }

        return ResponseResult.error().code(500);  // TODO 有待完善
    }

    /**
     * /api/hosp/department/remove
     * 根据id删除科室
     */
    @PostMapping("/department/remove")
    public ResponseResult departmentRemove(HttpServletRequest request){
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(request.getParameterMap());

        String hoscode = (String) stringObjectMap.get("hoscode");
        String depcode = (String) stringObjectMap.get("depcode");

        // 医院的密钥
        String hospitalSignkey = (String) stringObjectMap.get("sign");
        // 平台的密钥
        String hospitalSetSingKey = hospitalSetService.getSignKey(hoscode);
        // 非空校验
        if (!StringUtils.isEmpty(hospitalSetSingKey) && !StringUtils.isEmpty(hospitalSignkey)) {
            // 判断密钥是否相等
            if (MD5.encrypt(hospitalSetSingKey).equals(hospitalSignkey)) {
                departmentService.removeDepartment(depcode);
                // 如果b 为 true 移除成功
                return ResponseResult.success().code(200);  // TODO 有待完

            } else {
                throw new MyException();
            }
        }

        return ResponseResult.error().code(500);  // TODO 有待完善
    }


    /**
     * /api/hosp/saveSchedule"
     * 上传排班信息
     */
    @PostMapping("/saveSchedule")
    public ResponseResult saveSchedule(HttpServletRequest request){
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(request.getParameterMap());

        String hoscode = (String) stringObjectMap.get("hoscode");

        // 医院的密钥
        String hospitalSignkey = (String) stringObjectMap.get("sign");
        // 平台的密钥
        String hospitalSetSingKey = hospitalSetService.getSignKey(hoscode);

        // 非空校验
        if (!StringUtils.isEmpty(hospitalSetSingKey) && !StringUtils.isEmpty(hospitalSignkey)) {
            // 判断密钥是否相等
            if (MD5.encrypt(hospitalSetSingKey).equals(hospitalSignkey)) {
                scheduleService.save(stringObjectMap);
                return ResponseResult.success().code(200);  // TODO 有待完善
            } else {
                throw new MyException();
            }
        }

        return ResponseResult.error().code(500);  // TODO 有待完善
    }

    /**
     * /api/hosp/schedule/list
     * 排班列表
     */
    @PostMapping("/schedule/list")
    public ResponseResult scheduleList(HttpServletRequest request){
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(request.getParameterMap());

        String hoscode = (String) stringObjectMap.get("hoscode");
        String page = (String) stringObjectMap.get("page");
        String limit = (String) stringObjectMap.get("limit");

        // 医院的密钥
        String hospitalSignkey = (String) stringObjectMap.get("sign");
        // 平台的密钥
        String hospitalSetSingKey = hospitalSetService.getSignKey(hoscode);

        // 非空校验
        if (!StringUtils.isEmpty(hospitalSetSingKey) && !StringUtils.isEmpty(hospitalSignkey)) {
            // 判断密钥是否相等
            if (MD5.encrypt(hospitalSetSingKey).equals(hospitalSignkey)) {
                Page<Schedule> schedulePage = scheduleService.getSchedulePage(hoscode,page,limit);
                return ResponseResult.success().code(200).data("schedules", schedulePage);  // TODO 有待完善
            } else {
                throw new MyException();
            }
        }

        return ResponseResult.error().code(500);  // TODO 有待完善
    }

    /**
     * /api/hosp/schedule/remove
     * 移除排班人员 根据排班编号 hosScheduleId
     */
    @PostMapping("/schedule/remove")
    public ResponseResult scheduleRemove(HttpServletRequest request){
        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(request.getParameterMap());

        String hoscode = (String) stringObjectMap.get("hoscode");
        String hosScheduleId = (String) stringObjectMap.get("hosScheduleId");

        // 医院的密钥
        String hospitalSignkey = (String) stringObjectMap.get("sign");
        // 平台的密钥
        String hospitalSetSingKey = hospitalSetService.getSignKey(hoscode);
        // 非空校验
        if (!StringUtils.isEmpty(hospitalSetSingKey) && !StringUtils.isEmpty(hospitalSignkey)) {
            // 判断密钥是否相等
            if (MD5.encrypt(hospitalSetSingKey).equals(hospitalSignkey)) {
                scheduleService.removeSchedule(hosScheduleId);
                return ResponseResult.success().code(200);  // TODO 有待完

            } else {
                throw new MyException();
            }
        }

        return ResponseResult.error().code(500);  // TODO 有待完善
    }
}
