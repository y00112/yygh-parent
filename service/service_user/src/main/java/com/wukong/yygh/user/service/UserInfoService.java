package com.wukong.yygh.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.wukong.yygh.model.user.UserInfo;
import com.wukong.yygh.vo.user.LoginVo;

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
}
