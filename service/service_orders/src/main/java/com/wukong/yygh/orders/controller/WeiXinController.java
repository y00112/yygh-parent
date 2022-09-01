package com.wukong.yygh.orders.controller;

import com.wukong.yygh.common.result.ResponseResult;
import com.wukong.yygh.enums.PaymentTypeEnum;
import com.wukong.yygh.orders.service.PaymentService;
import com.wukong.yygh.orders.service.WeiXinPayService;
import io.swagger.annotations.ApiOperation;
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

    @Autowired
    private PaymentService paymentService;
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

    /**
     * 根据订单编号查询订单状态
     */
    @ApiOperation(value = "查询支付状态")
    @GetMapping("/queryPayStatus/{orderId}")
    public ResponseResult queryPayStatus(@PathVariable(value = "orderId") Long orderId){
        Map<String,String> resultMap = weiXinPayService.queryPayStatus(orderId);
        if (null == resultMap){
            return ResponseResult.error().message("支付失败");
        }
        if (null != resultMap && "SUCCESS".equals(resultMap.get("trade_state"))){
            // TODO
            //1、更新订单表的订单状态   orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());
            //2、更新支付表中的支付状态 paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus());

            String out_trade_no = resultMap.get("out_trade_no"); // 订单交易号
            paymentService.paySuccess(out_trade_no, PaymentTypeEnum.WEIXIN.getStatus(), resultMap);
            return ResponseResult.success().message("支付成功");
        }
        return ResponseResult.success().message("支付中");
    }
}
