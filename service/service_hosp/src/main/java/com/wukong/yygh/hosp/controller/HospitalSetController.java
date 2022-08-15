package com.wukong.yygh.hosp.controller;


import com.wukong.yygh.common.result.ResponseResult;
import com.wukong.yygh.hosp.service.HospitalSetService;
import com.wukong.yygh.model.hosp.HospitalSet;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 医院设置表 前端控制器
 * </p>
 *
 * @author wukong
 * @since 2022-08-14
 */
@Api(tags = "预约设置接口")
@RestController
@RequestMapping("/hosp/hospital-set")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;


    /**
     * 查询所有医院信息
     */
    @ApiOperation(value = "查询所有医院信息")
    @GetMapping("/hospitalInfo")
    public ResponseResult findAll() {
        List<HospitalSet> list = hospitalSetService.list();
        return ResponseResult.success().data("items",list);
    }

    /**
     * 删除医院信息根据ID（逻辑删除）
     */
    @ApiOperation(value = "删除医院信息根据ID")
    @DeleteMapping("/hospitalInfo/{id}")
    public ResponseResult removeById(@PathVariable Integer id) {
        boolean b = hospitalSetService.removeById(id);
        if (b){
            return ResponseResult.success().success(b);
        }
        return ResponseResult.error().success(b);

    }
}

