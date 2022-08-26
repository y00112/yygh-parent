package com.wukong.yygh.user.controller;


import com.wukong.yygh.common.result.ResponseResult;
import com.wukong.yygh.user.service.UserInfoService;
import com.wukong.yygh.vo.user.LoginVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author wukong
 * @since 2022-08-26
 */
@RestController
@RequestMapping("/user/userinfo")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 用户登录
     */
    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public ResponseResult login(@RequestBody LoginVo loginVo){

        Map<String,Object> map = userInfoService.login(loginVo);
        return ResponseResult.success().data(map);
    }
}

