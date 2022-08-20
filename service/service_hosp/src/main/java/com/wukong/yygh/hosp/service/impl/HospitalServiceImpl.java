package com.wukong.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.wukong.yygh.hosp.repository.DepartmentRepository;
import com.wukong.yygh.hosp.repository.HospitalRepository;
import com.wukong.yygh.hosp.service.HospitalService;
import com.wukong.yygh.model.hosp.Department;
import com.wukong.yygh.model.hosp.Hospital;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * Created By WuKong on 2022/8/19 14:58
 **/
@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;


    @Override
    public void save(Map<String, Object> stringObjectMap) {
        String stringJson = JSONObject.toJSONString(stringObjectMap);
        Hospital hospital = JSONObject.parseObject(stringJson, Hospital.class);

        // 1、根据医院编号查询医院信息
        Hospital hos =  hospitalRepository.findByHoscode(hospital.getHoscode());
        // 2、如果有医院信息就做更新操作，没有就做添加操作
        if (null == hos){
            //0：未上线 1：已上线
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }else {
            // 更新操作
            hospital.setStatus(hos.getStatus());
            hospital.setIsDeleted(hos.getIsDeleted());
            hospital.setUpdateTime(new Date());
            // 根据 hoscode查询 id
            hospital.setId(hos.getId());
            hospitalRepository.save(hospital);

        }

    }

    @Override
    public Hospital getHospital(String hoscode) {
        return hospitalRepository.findByHoscode(hoscode);
    }


}
