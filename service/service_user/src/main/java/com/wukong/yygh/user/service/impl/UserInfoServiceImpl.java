package com.wukong.yygh.user.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wukong.yygh.common.exception.MyException;
import com.wukong.yygh.common.utils.JwtHelper;
import com.wukong.yygh.enums.AuthStatusEnum;
import com.wukong.yygh.model.user.Patient;
import com.wukong.yygh.model.user.UserInfo;
import com.wukong.yygh.patient.service.PatientService;
import com.wukong.yygh.user.mapper.UserInfoMapper;
import com.wukong.yygh.user.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wukong.yygh.vo.user.LoginVo;
import com.wukong.yygh.vo.user.UserAuthVo;
import com.wukong.yygh.vo.user.UserInfoQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    private PatientService patientService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        //1 获取输入手机号和验证码
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();

        //2 手机号和验证码非空校验
        if(StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new MyException(20001,"数据为空");
        }

        //验证码校验
        // 输入的验证码 和 存储redis验证码比对
        String redisCode = redisTemplate.opsForValue().get(phone);
        if(!redisCode.equals(code)) {
            throw new MyException(20001,"验证码校验失败");
        }

        String openid = loginVo.getOpenid();
        Map<String, Object> map = new HashMap<>();
        if(StringUtils.isEmpty(openid)) {
            //根据手机查询数据库
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("phone",phone);
            UserInfo userInfo = baseMapper.selectOne(wrapper);
            //如果返回对象为空，就是第一次登录，存到数据库登录数据
            if(userInfo == null) {
                userInfo = new UserInfo();
                userInfo.setName("");
                userInfo.setPhone(phone);
                userInfo.setStatus(1);
                baseMapper.insert(userInfo);
            }
            //判断用户是否可用
            if(userInfo.getStatus() == 0) {
                throw new MyException(20001,"用户已经禁用");
            }
            map = get(userInfo);
        } else {
            //1 创建userInfo对象，用于存在最终所有数据
            UserInfo userInfoFinal = new UserInfo();

            //2 根据手机查询数据
            // 如果查询手机号对应数据,封装到userInfoFinal
            UserInfo userInfoPhone =
                    baseMapper.selectOne(new QueryWrapper<UserInfo>().eq("phone", phone));
            if(userInfoPhone != null) {
                // 如果查询手机号对应数据,封装到userInfoFinal
                BeanUtils.copyProperties(userInfoPhone,userInfoFinal);
                //把手机号数据删除
                baseMapper.delete(new QueryWrapper<UserInfo>().eq("phone", phone));
            }

            //3 根据openid查询微信信息
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("openid",openid);
            UserInfo userInfoWX = baseMapper.selectOne(wrapper);

            //4 把微信信息封装userInfoFinal
            userInfoFinal.setOpenid(userInfoWX.getOpenid());
            userInfoFinal.setNickName(userInfoWX.getNickName());
            userInfoFinal.setId(userInfoWX.getId());
            //数据库表没有相同绑定手机号，设置值
            if(userInfoPhone == null) {
                userInfoFinal.setPhone(phone);
                userInfoFinal.setStatus(userInfoWX.getStatus());
            }
            //修改手机号
            baseMapper.updateById(userInfoFinal);

            //5 判断用户是否锁定
            if(userInfoFinal.getStatus() == 0) {
                throw new MyException(20001,"用户被锁定");
            }
            //6 登录后，返回登录数据
            map = get(userInfoFinal);
        }
        return map;
    }

    private Map<String,Object> get(UserInfo userInfo) {
        //返回页面显示名称
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);
        //根据userid和name生成token字符串
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token", token);
        return map;
    }

    @Override
    public UserInfo selectByOpenId(String openid) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid",openid);
        UserInfo userInfo = baseMapper.selectOne(queryWrapper);
        return userInfo;
    }

    @Override
    public UserInfo getByIdParams(Long userId) {
        if (userId == null){
            return null;
        }
        UserInfo userInfo = baseMapper.selectById(userId);
        Map<String, Object> param = userInfo.getParam();
        param.put("authStatusString", AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        userInfo.setParam(param);
        return userInfo;
    }

    @Override
    public void saveUserAuthVo(Long userId, UserAuthVo userAuthVo) {
        // 设置认证信息
        UserInfo updateUserInfo = new UserInfo();
        updateUserInfo.setId(userId);
        // 认证人姓名
        updateUserInfo.setName(userAuthVo.getName());
        // 其他认证信息
        updateUserInfo.setCertificatesType(userAuthVo.getCertificatesType());
        updateUserInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        updateUserInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        updateUserInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());

        baseMapper.updateById(updateUserInfo);

    }

    @Override
    public Page<UserInfo> selectUserInfoPage(Integer page, Integer limit, UserInfoQueryVo userInfoQueryVo) {
        Page<UserInfo> userInfoPage = new Page<>(page,limit);
        //UserInfoQueryVo获取条件值
        String name = userInfoQueryVo.getKeyword(); //用户名称
        Integer status = userInfoQueryVo.getStatus();//用户状态
        Integer authStatus = userInfoQueryVo.getAuthStatus(); //认证状态
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin(); //开始时间
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd(); //结束时间
        //对条件值进行非空判断
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(name)) {
            wrapper.like("name",name).or().eq("phone",name);
        }
        if(!StringUtils.isEmpty(status)) {
            wrapper.eq("status",status);
        }
        if(!StringUtils.isEmpty(authStatus)) {
            wrapper.eq("auth_status",authStatus);
        }
        if(!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time",createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time",createTimeEnd);
        }
        Page<UserInfo> result = baseMapper.selectPage(userInfoPage, wrapper);
        result.getRecords().parallelStream().forEach(item->{
            packageUserInfo(item);
        });
        return result;
    }

    //实现方法
    @Override
    public void lock(Long userId, Integer status) {
        if(status.intValue() == 0 || status.intValue() == 1) {
            UserInfo userInfo = this.getById(userId);
            userInfo.setStatus(status);
            this.updateById(userInfo);
        }
    }

    @Override
    public Map<String, Object> show(Long userId) {
        Map<String,Object> map = new HashMap<>();
        //根据userid查询用户信息
        UserInfo userInfo1 = baseMapper.selectById(userId);
        packageUserInfo(userInfo1);
        map.put("userInfo", userInfo1);
        //根据userid查询就诊人信息
        List<Patient> patientList = patientService.getpatientList(userId);
        map.put("patientList",patientList);
        return map;
    }

    //认证审批  2通过  -1不通过
    @Override
    public void approval(Long userId, Integer authStatus) {
        if(authStatus.intValue()==2 || authStatus.intValue()==-1) {
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setAuthStatus(authStatus);
            baseMapper.updateById(userInfo);
        }
    }

    //编号变成对应值封装
    private void packageUserInfo(UserInfo userInfo) {
        //处理认证状态编码
        userInfo.getParam().put("authStatusString",AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        //处理用户状态 0  1
        String statusString = userInfo.getStatus().intValue()==0 ?"锁定" : "正常";
        userInfo.getParam().put("statusString",statusString);
//        return userInfo;
    }
}
