package com.wukong.yygh.listener;

import com.rabbitmq.client.Channel;
import com.wukong.yygh.rabbit.MQConst;
import com.wukong.yygh.service.MsmService;
import com.wukong.yygh.vo.msm.MsmVo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created By WuKong on 2022/8/31 16:49
 **/
@Component
public class SmsReceiver {
    @Autowired
    private MsmService msmService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConst.QUEUE_MSM_ITEM, durable = "true"),
            exchange = @Exchange(value = MQConst.EXCHANGE_DIRECT_MSM),
            key = {MQConst.ROUTING_MSM_ITEM}
    ))
    public void send(MsmVo msmVo, Message message, Channel channel) {
        msmService.send(msmVo);
    }
}