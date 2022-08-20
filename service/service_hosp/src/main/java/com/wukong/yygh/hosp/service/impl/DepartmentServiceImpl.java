package com.wukong.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.wukong.yygh.hosp.repository.DepartmentRepository;
import com.wukong.yygh.hosp.service.DepartmentService;
import com.wukong.yygh.model.hosp.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created By WuKong on 2022/8/19 19:16
 **/
@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void saveDepartment(Map<String, Object> stringObjectMap) {
        String stringJson = JSONObject.toJSONString(stringObjectMap);
        Department department = JSONObject.parseObject(stringJson, Department.class);

        // 1、查询MongoDB中是否有科室信息
        Department targetDepartment =  departmentRepository.findByHoscodeAndDepcode(department.getHoscode(),department.getDepcode());
        // 2、如果有医院信息就做更新操作，没有就做添加操作
        if (null == targetDepartment){
            // 添加操作
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }else {
            // 更新操作
            department.setCreateTime(targetDepartment.getCreateTime());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            department.setId(targetDepartment.getId());
            departmentRepository.save(department);
        }

    }

    @Override
    public Page<Department> getDepartmentPage(String hoscode, String page, String limit) {

        // 1、分页
        PageRequest pageable = PageRequest.of(Integer.valueOf(page) - 1, Integer.valueOf(limit));
        // 2、创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("hoscode", ExampleMatcher.GenericPropertyMatcher::exact) // 精确查询
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写
        // 3、分页条件
        Department department = new Department();
        department.setHoscode(hoscode);
        // 3、 创建查询实例
        Example<Department> departmentExample = Example.of(department,matcher);
        Page<Department> departmentPage = departmentRepository.findAll(departmentExample,pageable);

        return departmentPage;
    }

    @Override
    public void removeDepartment(String depcode) {
        departmentRepository.deleteDepartmentByDepcode(depcode);
    }
}
