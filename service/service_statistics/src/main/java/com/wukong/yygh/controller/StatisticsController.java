package com.wukong.yygh.controller;

import com.wukong.yygh.common.result.ResponseResult;
import com.wukong.yygh.order.client.OrderFeignClient;
import com.wukong.yygh.vo.order.OrderCountQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
  * Created By WuKong on 2022/9/1 19:23
  **/
@Api(tags = "统计管理接口")
@RestController
@RequestMapping("/admin/statistics")
public class StatisticsController {

    @Autowired
    private OrderFeignClient orderFeignClient;

    @ApiOperation(value = "获取订单统计数据")
    @PostMapping("/getCountMap")
    public ResponseResult getCountMap(@ApiParam(name = "orderCountQueryVo", value = "查询对象", required = false) OrderCountQueryVo orderCountQueryVo) {
        Map<String, Object> map = orderFeignClient.countOrderInfoByQuery(orderCountQueryVo);
        return ResponseResult.success().data(map);
    }
}