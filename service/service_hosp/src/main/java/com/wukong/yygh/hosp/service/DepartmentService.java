package com.wukong.yygh.hosp.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.wukong.yygh.model.hosp.Department;
import com.wukong.yygh.model.hosp.HospitalSet;
import com.wukong.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 医院设置表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2022-08-14
 */
public interface DepartmentService {

    void saveDepartment(Map<String, Object> stringObjectMap);

    Page<Department> getDepartmentPage(String hoscode, String page, String limit);

    void removeDepartment(String depcode);


    List<DepartmentVo> getAllDepts(String hoscode);

    Department getDepartByHoscodeAndDepcode(String hoscode, String depcode);
}
