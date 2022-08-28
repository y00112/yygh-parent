package com.wukong.yygh.oss.controller;

import com.wukong.yygh.common.result.ResponseResult;
import com.wukong.yygh.oss.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created By WuKong on 2022/8/28 19:48
 **/
@Api(description="阿里云文件管理")
@RestController
@RequestMapping("/admin/oss/file")
public class OssController {

    @Autowired
    private FileService fileService;

    /**
     * 文件上传
     */
    @ApiOperation(value = "文件上传")
    @PostMapping("upload")
    public ResponseResult upload(
            @ApiParam(name = "file", value = "文件", required = true)
            @RequestParam("file") MultipartFile file) {

        String uploadUrl = fileService.uploadFile(file);
        return ResponseResult.success().message("文件上传成功").data("url", uploadUrl);

    }
}
