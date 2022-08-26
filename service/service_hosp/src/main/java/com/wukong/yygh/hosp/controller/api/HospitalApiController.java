package com.wukong.yygh.hosp.controller.api;

import com.wukong.yygh.common.result.ResponseResult;
import com.wukong.yygh.hosp.service.DepartmentService;
import com.wukong.yygh.hosp.service.HospitalService;
import com.wukong.yygh.model.hosp.Hospital;
import com.wukong.yygh.vo.hosp.DepartmentVo;
import com.wukong.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created By WuKong on 2022/8/26 16:56
 **/
@Api(tags = "医院显示接口")
@RestController
@RequestMapping("/api/hosp/hospital")
public class HospitalApiController {

    @Autowired
    private HospitalService hospitalService;


    @Autowired
    private DepartmentService departmentService;

    /**
     * 根据hoscode医院编号，查询科室信息
     */
    @ApiOperation(value = "根据hoscode医院编号，查询科室信息")
    @GetMapping("/department/list/{hoscode}")
    public ResponseResult getDepartmentListByHoscode(@PathVariable String hoscode){
        List<DepartmentVo> allDepts = departmentService.getAllDepts(hoscode);
        return ResponseResult.success().data("list",allDepts);
    }

    /**
     * 根据hoscode医院编号，查询医院信息
     */
    @ApiOperation(value = "根据hoscode医院编号，查询医院信息")
    @GetMapping("/hospital/list/{hoscode}")
    public ResponseResult getHospitalListByHoscode(@PathVariable String hoscode){
        Hospital hospital = hospitalService.getHospital(hoscode);
        return ResponseResult.success().data("hospital",hospital);
     }

    /**
     *获取首页医院列表 ：带查询条件
     */
    @ApiOperation(value = "获取首页医院列表")
    @GetMapping("/{page}/{limit}")
    public ResponseResult getHospitalPage(@PathVariable(value = "page") String page,
                                          @PathVariable(value = "limit") String limit,
                                          HospitalQueryVo hospitalQueryVo){
        Page<Hospital> hospitals = hospitalService.selectPage(page, limit, hospitalQueryVo);
        return ResponseResult.success().data("pages",hospitals);
    }

    /**
     *  根据医院名称进行模糊查询
     */
    @ApiOperation(value = "获取首页医院列表")
    @GetMapping("/findByNameLike/{name}")
    public ResponseResult getPageList(@PathVariable String name){
        List<Hospital> hospitalList = hospitalService.findByNameLike(name);
        return ResponseResult.success().data("list",hospitalList);
    }
}
