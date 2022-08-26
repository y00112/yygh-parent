package com.wukong.yygh.hosp.controller;

import com.wukong.yygh.common.result.ResponseResult;
import com.wukong.yygh.hosp.service.DepartmentService;
import com.wukong.yygh.vo.hosp.DepartmentVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created By WuKong on 2022/8/25 15:13
 * 科室的前端控制器
 **/
@RequestMapping("/admin/hosp/department")
@RestController
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * 根据医院编号，查询该医院底下的所有科室信息：返回的数据是大科室底下嵌套子科室
     */
    @ApiOperation(value = "查询所有科室列表")
    @GetMapping("/getDeptList/{hoscode}")
    public ResponseResult getAllDept(@PathVariable(value = "hoscode") String hoscode){

        List<DepartmentVo> departmentVoList = departmentService.getAllDepts(hoscode);
        return ResponseResult.success().data("list",departmentVoList);
    }
}
