package com.wukong.yygh.orders.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wukong.yygh.model.order.OrderInfo;
import com.wukong.yygh.model.order.PaymentInfo;

/**
 * Created By WuKong on 2022/8/31 18:52
 **/
public interface PaymentService  extends IService<PaymentInfo> {

    /**
     * 保存交易记录
     * @param order
     * @param paymentType 支付类型（1：微信 2：支付宝）
     */
    void savePaymentInfo(OrderInfo order, Integer paymentType);
}
