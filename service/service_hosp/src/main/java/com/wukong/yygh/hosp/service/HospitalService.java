package com.wukong.yygh.hosp.service;

import com.wukong.yygh.model.hosp.Hospital;

import java.util.Map;

/**
 * Created By WuKong on 2022/8/19 14:58
 **/
public interface HospitalService {
    void save(Map<String, Object> stringObjectMap);


    Hospital getHospital(String hoscode);


}
