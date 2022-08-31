package com.wukong.yygh.orders.controller;

import com.wukong.yygh.common.result.ResponseResult;
import com.wukong.yygh.orders.service.WeiXinPayService;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created By WuKong on 2022/8/31 18:21
 **/
@RestController
@RequestMapping("/api/order/weixin")
public class WeiXinController {

    @Autowired
    private WeiXinPayService weiXinPayService;
    /**
     * 根据订单id生成二维码
     */
    @GetMapping("/createNative/{orderId}")
    public ResponseResult createNative(
            @ApiParam(name = "orderId", value = "订单id", required = true)
            @PathVariable("orderId") Long orderId) {
        Map map = weiXinPayService.createNative(orderId);
        return ResponseResult.success().data(map);
    }
}
