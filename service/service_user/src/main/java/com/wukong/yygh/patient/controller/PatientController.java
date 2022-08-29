package com.wukong.yygh.patient.controller;


import com.wukong.yygh.common.result.ResponseResult;
import com.wukong.yygh.model.user.Patient;
import com.wukong.yygh.patient.service.PatientService;
import com.wukong.yygh.user.utils.AuthContextHolder;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 就诊人表 前端控制器
 * </p>
 *
 * @author wukong
 * @since 2022-08-29
 */
@RestController
@RequestMapping("/patient/userinfo")
public class PatientController {

    @Autowired
    private PatientService patientService;

    /**
     * 添加就诊人
     */
    @PostMapping("/auth/save")
    public ResponseResult savePatient(HttpServletRequest request,
                                      @RequestBody Patient patient){
        // 通过request获取用户id
        Long userId = AuthContextHolder.getUserId(request);
        patient.setUserId(userId);
        patientService.save(patient);
        return ResponseResult.success();
    }

    /**
     * 删除就诊人信息
     */
    @DeleteMapping("/auth/remove/{id}")
    public ResponseResult removeById(@PathVariable Integer id){
        patientService.removeById(id);
        return ResponseResult.success();
    }


    /**
     * 获取就诊人信息
     */
    @GetMapping("/auth/get/{id}")
    public ResponseResult getPatientInfo(@PathVariable("id") Integer pid){
        Patient patient = patientService.getPatientById(pid);
        return ResponseResult.success().data("patient",patient);
    }

    /**
     * 修改就诊人信息
     */
    @PutMapping("/auth/update")
    public ResponseResult updatePatient(@RequestBody Patient patient){
        patientService.updateById(patient);
        return ResponseResult.success();
    }

    /**
     * 查询当前登录用户底下所有就诊人信息
     */
    @GetMapping("/auth/findAll")
    public ResponseResult getPatientList(HttpServletRequest request){
        Long userId = AuthContextHolder.getUserId(request);
        List<Patient> patientList = patientService.getpatientList(userId);
        return ResponseResult.success().data("list",patientList);
    }

}

