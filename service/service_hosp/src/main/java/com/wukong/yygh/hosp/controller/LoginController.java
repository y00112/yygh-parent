package com.wukong.yygh.hosp.controller;

import com.wukong.yygh.common.result.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created By WuKong on 2022/8/16 10:13
 * 临时登录
 **/
@RestController
@Slf4j
@RequestMapping("/yygh/user")
@CrossOrigin //允许跨域
public class LoginController {

    /**
     * 临时登录接口
     */
    @PostMapping("/login")
    public ResponseResult login(){
        return ResponseResult.success().data("token","admin-token");
    }

    /**
     * Info
     */
    @GetMapping("/info")
    public ResponseResult info(String token){
        log.info("token: {}",token);
        Map map = new HashMap<String,Object>();
        map.put("roles","[admin]");
        map.put("introduction","I am a super administrator");
        map.put("avatar","https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        map.put("name", "Super Admin");
        return ResponseResult.success().data(map);
    }
}
