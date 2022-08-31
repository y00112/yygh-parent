package com.wukong.yygh.client;

import com.wukong.yygh.common.result.ResponseResult;
import com.wukong.yygh.model.user.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created By WuKong on 2022/8/30 16:13
 **/
@FeignClient("service-user")
public interface UserFeignClient {

    // 获取就诊人信息
    @GetMapping("/patient/userinfo/auth/get/{id}")
    ResponseResult getPatientInfo(@PathVariable("id") String pid);
}
