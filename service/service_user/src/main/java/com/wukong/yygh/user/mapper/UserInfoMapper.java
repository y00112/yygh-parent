package com.wukong.yygh.user.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wukong.yygh.model.user.UserInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author wukong
 * @since 2022-08-26
 */
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {

}
