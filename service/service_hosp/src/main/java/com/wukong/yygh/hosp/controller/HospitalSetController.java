package com.wukong.yygh.hosp.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wukong.yygh.common.exception.MyException;
import com.wukong.yygh.common.result.ResponseResult;
import com.wukong.yygh.hosp.service.HospitalSetService;
import com.wukong.yygh.hosp.util.MD5;
import com.wukong.yygh.model.hosp.HospitalSet;
import com.wukong.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
@RequestMapping("/admin/hosp/hospital-set")
//@CrossOrigin
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;


    /**
     * 查询所有医院信息
     */
    @ApiOperation(value = "查询所有医院信息")
    @GetMapping("/hospitalInfo")
    public ResponseResult findAll() {

//        int a = 10/0;
        List<HospitalSet> list = hospitalSetService.list();
        return ResponseResult.success().data("items",list);
    }

    /**
     * 分页条件医院设置列表
     */
    @ApiOperation(value = "分页条件医院设置列表")
    @PostMapping("/hospitalInfo/{current}/{limit}")
    public ResponseResult pageList(@PathVariable Integer current,
                                   @PathVariable Integer limit,
                                   @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo){

        //定义 QueryWrapper 类似于 sql语句的 where
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        //判断表单提交的查询条件是否为空
        if (null != hospitalSetQueryVo){
            String hosname = hospitalSetQueryVo.getHosname();
            String hoscode = hospitalSetQueryVo.getHoscode();


            if (!StringUtils.isEmpty(hosname)){
                queryWrapper.like("hosname",hosname);
            }

            if (!StringUtils.isEmpty(hoscode)){
                queryWrapper.eq("hoscode ",hoscode);
            }
        }


        Page<HospitalSet> page = new Page<HospitalSet>(current,limit);
        Page pageList = hospitalSetService.page(page, queryWrapper);

        Map<String,Object> map = new HashMap<String,Object>();
        map.put("total",pageList.getTotal());
        map.put("hasPrevious",pageList.hasPrevious());
        map.put("hasNext",pageList.hasNext());
        map.put("records",pageList.getRecords());

        return ResponseResult.success().data(map);
    }

    /**
     * 根据医院id查询医院详情
     */
    @ApiOperation(value = "根据ID查询医院设置")
    @GetMapping("getHospSet")
    public ResponseResult geHospitalSetDetail(@RequestParam("id") Integer id){
        HospitalSet hosp = hospitalSetService.getById(id);
        return ResponseResult.success().data("item",hosp);
    }

    /**
     * 根据ID修改医院设置
     */
    @ApiOperation(value = "根据ID修改医院设置")
    @PostMapping("updateHospSet")
    public ResponseResult updateById(@RequestBody HospitalSet hospitalSet){
        boolean update = hospitalSetService.updateById(hospitalSet);
        if (update){
            return ResponseResult.success().message("修改成功");
        }
        return ResponseResult.error().message("修改失败");
    }

    // 医院设置锁定和解锁
    @PutMapping("lockHospitalSet/{id}/{status}")
    public ResponseResult lockHospitalSet(@PathVariable Long id,
                             @PathVariable Integer status) {
        //根据id查询医院设置信息
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        //设置状态
        hospitalSet.setStatus(status);
        //调用方法
        hospitalSetService.updateById(hospitalSet);
        return ResponseResult.success();
    }

    /**
     * 添加医院信息
     */
    @ApiOperation(value = "添加医院信息")
    @PostMapping("/saveHospSet")
    public ResponseResult save(@RequestBody HospitalSet hospitalSet) {
        hospitalSet.setStatus(0);
        //签名密钥
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+"")+ new Random().nextInt(10000));
        boolean save = hospitalSetService.save(hospitalSet);
        if (save){
            return ResponseResult.success().message("添加成功");
        }
        return ResponseResult.error().message("添加失败");

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

    /**
     * 批量删除
     */
    @DeleteMapping("/batchRemove")
    public ResponseResult batchRemoveHospSet(@RequestBody List<Long> ids){
        boolean delete = hospitalSetService.removeByIds(ids);
        if (delete){
            return ResponseResult.success().message("批量删除成功");
        }
        return ResponseResult.error().message("批量删除失败");
    }
}

