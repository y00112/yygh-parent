package com.wukong.yygh.orders.listener;

import com.rabbitmq.client.Channel;
import com.wukong.yygh.orders.service.OrderInfoService;
import com.wukong.yygh.rabbit.MQConst;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created By WuKong on 2022/9/1 17:08
 **/
@Component
public class OrderReceive {

    @Autowired
    private OrderInfoService orderService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConst.QUEUE_TASK_8, durable = "true"),
            exchange = @Exchange(value = MQConst.EXCHANGE_DIRECT_TASK),
            key = {MQConst.ROUTING_TASK_8}
    ))
    public void patientTips(Message message, Channel channel) throws IOException {
        orderService.patientTips();
    }
}
