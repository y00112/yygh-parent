package com.wukong.yygh.user.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wukong.yygh.common.exception.MyException;
import com.wukong.yygh.common.utils.JwtHelper;
import com.wukong.yygh.model.user.UserInfo;
import com.wukong.yygh.user.mapper.UserInfoMapper;
import com.wukong.yygh.user.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wukong.yygh.vo.user.LoginVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

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
        String redisCode = redisTemplate.opsForValue().get(phone);
        if (!code.equals(redisCode)){
            throw new MyException(20001,"验证码错误");
        }
        UserInfo userInfo = null;
        String openid = loginVo.getOpenid();
        // 4、判断是不是首次登录，如果是首次登录，完成自动注册功能、
        if (StringUtils.isEmpty(openid)){ // 手机号登录
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone",phone);
            userInfo= baseMapper.selectOne(queryWrapper);
        if (null == userInfo){
            userInfo = new UserInfo();
            userInfo.setPhone(phone);
            userInfo.setStatus(1);
            userInfo.setCreateTime(new Date());
            userInfo.setUpdateTime(new Date());
            baseMapper.insert(userInfo);
        }
        }else { // 微信的登录
            UserInfo userInfoFinal = new UserInfo();
            QueryWrapper<UserInfo> phoneWrapper = new QueryWrapper<>();
            phoneWrapper.eq("phone",phone);
            UserInfo phoneInfo = baseMapper.selectOne(phoneWrapper);
            if (null != phoneInfo){
                BeanUtils.copyProperties(phoneInfo,userInfoFinal);
                baseMapper.delete(phoneWrapper);
            }

            // 根据openid查询微信信息
            QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("openid",openid);
            userInfo= baseMapper.selectOne(queryWrapper);
            //
            userInfoFinal.setId(userInfo.getId());
            userInfoFinal.setOpenid(userInfo.getOpenid());
            userInfoFinal.setNickName(userInfo.getNickName());
            userInfoFinal.setStatus(userInfo.getStatus());

            baseMapper.updateById(userInfoFinal);
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

        // 生成token
        String token = JwtHelper.createToken(userInfo.getId(), userInfo.getName());

        map.put("name", name);
        map.put("token",token);
        return map;
    }

    @Override
    public UserInfo selectByOpenId(String openid) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid",openid);
        UserInfo userInfo = baseMapper.selectOne(queryWrapper);
        return userInfo;
    }
}
