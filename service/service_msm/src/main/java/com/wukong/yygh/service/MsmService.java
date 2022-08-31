package com.wukong.yygh.service;

import com.wukong.yygh.vo.msm.MsmVo;

/**
 * Created By WuKong on 2022/8/27 9:53
 **/
public interface MsmService {
    boolean sendCode(String phone);

    void send(MsmVo msmVo);
}
