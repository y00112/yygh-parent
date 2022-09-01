package com.wukong.yygh.orders.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wukong.yygh.model.order.PaymentInfo;
import com.wukong.yygh.model.order.RefundInfo;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 退款信息表 服务类
 * </p>
 *
 * @author wukong
 * @since 2022-09-01
 */
@Service
public interface RefundInfoService extends IService<RefundInfo> {

    RefundInfo saveReFund(PaymentInfo paymentInfo);
}
