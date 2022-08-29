package com.wukong.yygh.user.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wukong.yygh.common.result.ResponseResult;
import com.wukong.yygh.model.user.UserInfo;
import com.wukong.yygh.user.service.UserInfoService;
import com.wukong.yygh.vo.user.UserInfoQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created By WuKong on 2022/8/29 19:56
 **/
@RestController
@RequestMapping("/admin/userinfo")
public class UserController {

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 用户列表
     */
    @GetMapping("/{page}/{limit}")
    public ResponseResult getUserInfoPage(@PathVariable Integer page,
                                          @PathVariable Integer limit,
                                          UserInfoQueryVo userInfoQueryVo){
        Page<UserInfo> pages = userInfoService.selectUserInfoPage(page,limit,userInfoQueryVo);
        return ResponseResult.success().data("pageList",pages);

    }

    /**
     * 锁定和解锁
     */
    @ApiOperation(value = "锁定")
    @GetMapping("/lock/{userId}/{status}")
    public ResponseResult lock(
            @PathVariable("userId") Long userId,
            @PathVariable("status") Integer status){
        userInfoService.lock(userId, status);
        return ResponseResult.success();
    }

    /**
     * 用户详情
     */
    @GetMapping("/show/{userId}")
    public ResponseResult show(@PathVariable Long userId) {
        Map<String,Object> map = userInfoService.show(userId);
        return ResponseResult.success().data(map);
    }

    /**
     * 用户认证审批
     */
    @GetMapping("approval/{userId}/{authStatus}")
    public ResponseResult approval(@PathVariable Long userId,@PathVariable Integer authStatus) {
        userInfoService.approval(userId,authStatus);
        return ResponseResult.success();
    }
}
