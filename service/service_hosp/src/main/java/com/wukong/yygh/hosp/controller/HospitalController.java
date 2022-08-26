package com.wukong.yygh.hosp.controller;

import com.wukong.yygh.common.result.ResponseResult;
import com.wukong.yygh.hosp.service.HospitalService;
import com.wukong.yygh.model.hosp.Hospital;
import com.wukong.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * Created By WuKong on 2022/8/23 17:29
 * 医院接口
 **/
@RestController
@Api(description = "医院接口")
@RequestMapping("/admin/hosp/hospital")
//@CrossOrigin
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    /**
     * 获取医院分页列表
     */
    @ApiOperation(value = "获取分页列表")
    @GetMapping("/{page}/{limit}")
    public ResponseResult getHospitalPage(@PathVariable(value = "page") String page,
                                          @PathVariable(value = "limit") String limit,
                                           HospitalQueryVo hospitalQueryVo){
        Page<Hospital> hospitalPage =  hospitalService.selectPage(page,limit,hospitalQueryVo);

        return ResponseResult.success().data("list",hospitalPage);
    }

    /**
     * 更新医院上线 下线状态
     */
    @PutMapping("/{id}/{status}")
    public ResponseResult updateHospStatus(@PathVariable(value = "id") String id,
                                           @PathVariable(value = "status") Integer status)
    {
        hospitalService.updateStatus(id,status);
        return ResponseResult.success();
    }

    /**
     * 根据 医院id 获取医院信息
     */
    @GetMapping("/detail/{id}")
    public ResponseResult getHospitalById(@PathVariable("id") String id){
        Hospital hospital = hospitalService.getHospitalById(id);
        return  ResponseResult.success().data("hospital",hospital).data("bookingRule",hospital.getBookingRule());
    }

}
