package com.wukong.yygh.patient.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wukong.yygh.client.DictFeignClient;
import com.wukong.yygh.enums.DictEnum;
import com.wukong.yygh.model.user.Patient;
import com.wukong.yygh.patient.mapper.PatientMapper;
import com.wukong.yygh.patient.service.PatientService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 就诊人表 服务实现类
 * </p>
 *
 * @author wukong
 * @since 2022-08-29
 */
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public Patient getPatientById(Integer pid) {
        Patient patient = baseMapper.selectById(pid);
        Patient resultPatient = packPatient(patient);
        return resultPatient;
    }

    @Override
    public List<Patient> getpatientList(Long userId) {
        QueryWrapper<Patient> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        List<Patient> patientList = baseMapper.selectList(queryWrapper);
        return patientList;
    }

    private Patient packPatient(Patient patient) {
        //根据证件类型编码，获取证件类型具体指
        String certificatesTypeString =
                dictFeignClient.getName(patient.getCertificatesType());//联系人证件
        //联系人证件类型
        // String contactsCertificatesTypeString =dictFeignClient.getName(DictEnum.CERTIFICATES_TYPE.getDictCode(),patient.getContactsCertificatesType());
        //省
        String provinceString = dictFeignClient.getName(patient.getProvinceCode());
        //市
        String cityString = dictFeignClient.getName(patient.getCityCode());
        //区
        String districtString = dictFeignClient.getName(patient.getDistrictCode());
        patient.getParam().put("certificatesTypeString", certificatesTypeString);
        // patient.getParam().put("contactsCertificatesTypeString", contactsCertificatesTypeString);
        patient.getParam().put("provinceString", provinceString);
        patient.getParam().put("cityString", cityString);
        patient.getParam().put("districtString", districtString);
        patient.getParam().put("fullAddress", provinceString + cityString + districtString + patient.getAddress());
        return patient;
    }
}
