package com.wukong.yygh.dict.controller;


import com.wukong.yygh.common.result.ResponseResult;
import com.wukong.yygh.dict.service.DictService;
import com.wukong.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 组织架构表 前端控制器
 * </p>
 * 字典模块
 *
 * @author wukong
 * @since 2022-08-17
 */
@RestController
@RequestMapping("/admin/cmn/dict")
@CrossOrigin
@Api(tags = "数据资料模块控制层")
@Slf4j
public class DictController {

    @Autowired
    private DictService dictService;

    /**
     * 导入excel
     */
    @PostMapping("/importData")
    public ResponseResult importData(MultipartFile file){
        //解析 excel文件
        dictService.importData(file);
        return ResponseResult.success();
    }

    /**
     * 导出excel
     */
    @GetMapping("/exportExcel")
    public void exportExcel(HttpServletResponse response){
        dictService.exportExcel(response);
    }

    /**
     *根据pid查询子元素类别
     */
    @ApiOperation(value = "根据pid查询子元素类别")
    @GetMapping("/childList/{id}")
    public ResponseResult getDictListByPid(@PathVariable Long id){
        log.info("id======>",id);
        List<Dict> dickList = dictService.getDickListByPid(id);

        return ResponseResult.success().data("list",dickList);
    }
}

