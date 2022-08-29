package com.wukong.yygh.patient.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wukong.yygh.model.user.Patient;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 就诊人表 Mapper 接口
 * </p>
 *
 * @author wukong
 * @since 2022-08-29
 */
@Mapper
public interface PatientMapper extends BaseMapper<Patient> {

}
