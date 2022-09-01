package com.wukong.yygh.orders.service;

import java.util.Map;

/**
 * Created By WuKong on 2022/8/31 18:23
 **/
public interface WeiXinPayService {
    Map createNative(Long orderId);

    Map<String, String> queryPayStatus(Long orderId);

    boolean refund(Long orderId);
}
