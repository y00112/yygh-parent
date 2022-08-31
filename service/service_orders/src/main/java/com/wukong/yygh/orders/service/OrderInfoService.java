package com.wukong.yygh.orders.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wukong.yygh.model.order.OrderInfo;
import com.wukong.yygh.vo.order.OrderQueryVo;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author wukong
 * @since 2022-08-30
 */
public interface OrderInfoService extends IService<OrderInfo> {

    Long saveOrder(String scheduleId, String patientId);

    IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo);

    OrderInfo getOrderInfo(Long orderId);
}
