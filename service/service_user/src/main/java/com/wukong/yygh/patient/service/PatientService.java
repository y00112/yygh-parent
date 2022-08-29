package com.wukong.yygh.patient.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.wukong.yygh.model.user.Patient;

import java.util.List;

/**
 * <p>
 * 就诊人表 服务类
 * </p>
 *
 * @author wukong
 * @since 2022-08-29
 */
public interface PatientService extends IService<Patient> {

    Patient getPatientById(Integer pid);

    List<Patient> getpatientList(Long userId);
}
