package com.wukong.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.wukong.yygh.hosp.repository.DepartmentRepository;
import com.wukong.yygh.hosp.service.DepartmentService;
import com.wukong.yygh.model.hosp.Department;
import com.wukong.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public List<DepartmentVo> getAllDepts(String hoscode) {

        //创建list集合，用于最终数据封装
        List<DepartmentVo> result = new ArrayList<>();

        //根据医院编号，查询医院所有科室信息
        Department departmentQuery = new Department();
        departmentQuery.setHoscode(hoscode);
        Example example = Example.of(departmentQuery);
        //所有科室列表 departmentList
        List<Department> departmentList = departmentRepository.findAll(example);

        //根据大科室编号  bigcode 分组，获取每个大科室里面下级子科室
        Map<String, List<Department>> deparmentMap =
                departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));
        //遍历map集合 deparmentMap
        for(Map.Entry<String,List<Department>> entry : deparmentMap.entrySet()) {
            //大科室编号
            String bigcode = entry.getKey();
            //大科室编号对应的全局数据
            List<Department> deparment1List = entry.getValue();
            //封装大科室
            DepartmentVo departmentVo1 = new DepartmentVo();
            departmentVo1.setDepcode(bigcode);
            departmentVo1.setDepname(deparment1List.get(0).getBigname());

            //封装小科室
            List<DepartmentVo> children = new ArrayList<>();
            for(Department department: deparment1List) {
                DepartmentVo departmentVo2 =  new DepartmentVo();
                departmentVo2.setDepcode(department.getDepcode());
                departmentVo2.setDepname(department.getDepname());
                //封装到list集合
                children.add(departmentVo2);
            }
            //把小科室list集合放到大科室children里面
            departmentVo1.setChildren(children);
            //放到最终result里面
            result.add(departmentVo1);
        }
        //返回
        return result;
    }

    @Override
    public Department getDepartByHoscodeAndDepcode(String hoscode, String depcode) {
        return departmentRepository.findByHoscodeAndDepcode(hoscode,depcode);
    }
}
