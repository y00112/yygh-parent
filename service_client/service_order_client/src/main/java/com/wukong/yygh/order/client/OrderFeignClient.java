package com.wukong.yygh.order.client;

import com.wukong.yygh.vo.order.OrderCountQueryVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * Created By WuKong on 2022/9/1 20:46
 **/
@FeignClient(value = "service-orders")
@Repository
public interface OrderFeignClient {
    /**
     * 获取订单统计数据
     */
    @PostMapping("/orders/order/countOrderInfoQuery")
    Map<String, Object> countOrderInfoByQuery(@RequestBody OrderCountQueryVo orderCountQueryVo);

}
