package com.wukong.yygh.orders.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wukong.yygh.common.result.ResponseResult;
import com.wukong.yygh.enums.OrderStatusEnum;
import com.wukong.yygh.model.order.OrderInfo;
import com.wukong.yygh.orders.service.OrderInfoService;
import com.wukong.yygh.orders.utils.AuthContextHolder;
import com.wukong.yygh.vo.order.OrderQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author wukong
 * @since 2022-08-30
 */
@RestController
@RequestMapping("/orders/order")
public class OrderInfoController {

    @Autowired
    private OrderInfoService orderInfoService;

    /**
     * 创建订单
     */
    @ApiOperation(value = "创建订单")
    @PostMapping("/auth/submitOrder/{scheduleId}/{patientId}")
    public ResponseResult submitOrder(@PathVariable(value = "scheduleId") String scheduleId,
                                      @PathVariable(value = "patientId") String patientId){

        Long orderId = orderInfoService.saveOrder(scheduleId,patientId);
        return ResponseResult.success().data("orderId",orderId);
    }

    /**
     * 查询用户订单信息
     * 订单列表（条件查询带分页）
     */
    @GetMapping("/auth/{page}/{limit}")
    public ResponseResult list(@PathVariable Long page,
                               @PathVariable Long limit,
                               OrderQueryVo orderQueryVo,
                               HttpServletRequest request) {
        //设置当前用户id
        orderQueryVo.setUserId(AuthContextHolder.getUserId(request));
        Page<OrderInfo> pageParam = new Page<>(page,limit);
        IPage<OrderInfo> pageModel =
                orderInfoService.selectPage(pageParam,orderQueryVo);
        return ResponseResult.success().data("pageModel",pageModel);
    }

    /**
     * 获取所有订单状态
     */
    @ApiOperation(value = "获取订单状态")
    @GetMapping("/auth/getStatusList")
    public ResponseResult getStatusList() {
        return ResponseResult.success().data("statusList", OrderStatusEnum.getStatusList());
    }

    /**
     * 根据订单id 查询订单信息
     */
    @GetMapping("auth/getOrders/{orderId}")
    public ResponseResult getOrders(@PathVariable Long orderId) {
        OrderInfo orderInfo = orderInfoService.getOrderInfo(orderId);
        return ResponseResult.success().data("orderInfo",orderInfo);
    }

}

