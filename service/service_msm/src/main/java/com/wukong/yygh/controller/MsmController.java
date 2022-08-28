package com.wukong.yygh.controller;

import com.wukong.yygh.common.result.ResponseResult;
import com.wukong.yygh.service.MsmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created By WuKong on 2022/8/27 9:51
 **/
@RestController
@RequestMapping("/api/msm")
public class MsmController {

    @Autowired
    private MsmService msmService;

    @PostMapping(value = "/send/{phone}")
    public ResponseResult sendCode(@PathVariable String phone){
        boolean b = msmService.sendCode(phone);
        if (b){
            return ResponseResult.success();
        }
        return ResponseResult.error().message("验证码发送失败！");
    }
}
