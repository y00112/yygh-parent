package com.wukong.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.wukong.yygh.client.DictFeignClient;
import com.wukong.yygh.enums.DictEnum;
import com.wukong.yygh.hosp.repository.DepartmentRepository;
import com.wukong.yygh.hosp.repository.HospitalRepository;
import com.wukong.yygh.hosp.service.HospitalService;
import com.wukong.yygh.model.hosp.Department;
import com.wukong.yygh.model.hosp.Hospital;
import com.wukong.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * Created By WuKong on 2022/8/19 14:58
 **/
@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;

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

    // 做带查询条件的分页
    @Override
    public Page<Hospital> selectPage(String page, String limit, HospitalQueryVo hospitalQueryVo) {

        //设置分页信息
        PageRequest pageRequest = PageRequest.of(Integer.valueOf(page)-1, Integer.valueOf(limit), Sort.by(Sort.Direction.ASC, "createTime"));


        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写

        //设置查询条件
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);
        Example example = Example.of(hospital,matcher);

        Page<Hospital> pageList = hospitalRepository.findAll(example, pageRequest);

        pageList.getContent().stream().forEach(item->{
            this.packHospital(item);
        });

        return pageList;
    }

    @Override
    public void updateStatus(String id, Integer status) {
        if (status.intValue() == 0 || status.intValue() ==1){
            Hospital hospital = hospitalRepository.findById(id).get();
            hospital.setStatus(status);
            hospital.setUpdateTime(new Date());
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Hospital getHospitalById(String id) {
       Hospital hospital = hospitalRepository.findById(id).get();
       packHospital(hospital);
       return hospital;
    }

    private void packHospital(Hospital item) {
        //医院等级
        String hostypeString = dictFeignClient.getName(DictEnum.HOSTYPE.getDictCode(),item.getHostype());
        // 省
        String provinceString = dictFeignClient.getName(item.getProvinceCode());
        // 市
        String cityString = dictFeignClient.getName(item.getCityCode());
        // 区
        String districtString = dictFeignClient.getName(item.getDistrictCode());

        item.getParam().put("hostypeString", hostypeString);
        item.getParam().put("fullAddress", provinceString + cityString + districtString + item.getAddress());
    }


}
