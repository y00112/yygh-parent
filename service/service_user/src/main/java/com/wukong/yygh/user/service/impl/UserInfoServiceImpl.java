package com.wukong.yygh.user.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wukong.yygh.common.exception.MyException;
import com.wukong.yygh.model.user.UserInfo;
import com.wukong.yygh.user.mapper.UserInfoMapper;
import com.wukong.yygh.user.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wukong.yygh.vo.user.LoginVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author wukong
 * @since 2022-08-26
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        // 1、获取用户输入的手机号 和 验证码
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();

        // 2、要对手机号和验证码进行非空校验
        if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)){
            throw new MyException(20001,"用户用户名或密码错误");
        }
        // 3、TODO：redis 的验证比较

        // 4、判断是不是首次登录，如果是首次登录，完成自动注册功能
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone",phone);
        UserInfo userInfo = baseMapper.selectOne(queryWrapper);
        if (null == userInfo){
            userInfo = new UserInfo();
            userInfo.setPhone(phone);
            userInfo.setStatus(1);
            userInfo.setCreateTime(new Date());
            userInfo.setUpdateTime(new Date());
            baseMapper.insert(userInfo);
        }
        // 5、判断用户的状态
        if (userInfo.getStatus() == 0){
            throw new MyException(20001,"该用户已被禁用");
        }

        Map<String,Object> map = new HashMap<>();
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);
        map.put("token","");
        return map;
    }
}
