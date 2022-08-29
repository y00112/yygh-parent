package com.wukong.yygh.user.controller;


import com.wukong.yygh.common.result.ResponseResult;
import com.wukong.yygh.common.utils.JwtHelper;
import com.wukong.yygh.model.user.UserInfo;
import com.wukong.yygh.user.service.UserInfoService;
import com.wukong.yygh.user.utils.AuthContextHolder;
import com.wukong.yygh.user.utils.ConstantPropertiesUtil;
import com.wukong.yygh.vo.user.LoginVo;
import com.wukong.yygh.vo.user.UserAuthVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
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
     * 保存用户人认证信息
     */
    @PostMapping("/auth/saveUserAuth")
    public ResponseResult saveAuth(HttpServletRequest request,
                                   @RequestBody UserAuthVo userAuthVo){

        // 从token中获取用户id
        Long userId = AuthContextHolder.getUserId(request);
        // 根据id 保存用户信息
        userInfoService.saveUserAuthVo(userId,userAuthVo);

        return ResponseResult.success();
    }

    /**
     * 用户登录
     */
    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public ResponseResult login(@RequestBody LoginVo loginVo){

        Map<String,Object> map = userInfoService.login(loginVo);
        return ResponseResult.success().data(map);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/auth/getUserInfo")
    public ResponseResult getUserInfo(HttpServletRequest request){
        Long userId = AuthContextHolder.getUserId(request);
        UserInfo userInfo = userInfoService.getByIdParams(userId);
        return ResponseResult.success().data("userInfo",userInfo);
    }


}

