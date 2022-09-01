package com.wukong.yygh.orders.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wukong.yygh.enums.RefundStatusEnum;
import com.wukong.yygh.model.order.PaymentInfo;
import com.wukong.yygh.model.order.RefundInfo;
import com.wukong.yygh.orders.mapper.RefundInfoMapper;
import com.wukong.yygh.orders.service.RefundInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 退款信息表 服务实现类
 * </p>
 *
 * @author wukong
 * @since 2022-09-01
 */
@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {

    @Override
    public RefundInfo saveReFund(PaymentInfo paymentInfo) {
        QueryWrapper<RefundInfo> paymentInfoQueryWrapper = new QueryWrapper<>();
        paymentInfoQueryWrapper.eq("order_id",paymentInfo.getOrderId());
        RefundInfo refundInfo = baseMapper.selectOne(paymentInfoQueryWrapper);
        if (refundInfo != null){
            return refundInfo;
        }
        RefundInfo newRefund = new RefundInfo();
        // 保存交易记录
        newRefund = new RefundInfo();
        newRefund.setCreateTime(new Date());
        newRefund.setOrderId(paymentInfo.getOrderId());
        newRefund.setPaymentType(paymentInfo.getPaymentType());
        newRefund.setOutTradeNo(paymentInfo.getOutTradeNo());
        newRefund.setRefundStatus(RefundStatusEnum.UNREFUND.getStatus());
        newRefund.setSubject(paymentInfo.getSubject());
        //paymentInfo.setSubject("test");
        newRefund.setTotalAmount(paymentInfo.getTotalAmount());
        baseMapper.insert(newRefund);
        return newRefund;
    }
}
