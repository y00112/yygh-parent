package com.wukong.yygh.user.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wukong.yygh.model.user.UserInfo;
import com.wukong.yygh.vo.user.LoginVo;
import com.wukong.yygh.vo.user.UserAuthVo;
import com.wukong.yygh.vo.user.UserInfoQueryVo;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author wukong
 * @since 2022-08-26
 */
public interface UserInfoService extends IService<UserInfo> {

    Map<String, Object> login(LoginVo loginVo);

    UserInfo selectByOpenId(String openid);

    UserInfo getByIdParams(Long userId);

    void saveUserAuthVo(Long userId, UserAuthVo userAuthVo);

    Page<UserInfo> selectUserInfoPage(Integer page, Integer limit, UserInfoQueryVo userInfoQueryVo);

    void lock(Long userId, Integer status);

    Map<String,Object> show(Long userId);

    void approval(Long userId, Integer authStatus);
}
