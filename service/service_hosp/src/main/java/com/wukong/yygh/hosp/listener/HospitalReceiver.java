package com.wukong.yygh.hosp.listener;


import com.rabbitmq.client.Channel;
import com.wukong.yygh.hosp.service.ScheduleService;
import com.wukong.yygh.model.hosp.Schedule;
import com.wukong.yygh.rabbit.MQConst;
import com.wukong.yygh.rabbit.RabbitService;
import com.wukong.yygh.vo.msm.MsmVo;
import com.wukong.yygh.vo.order.OrderMqVo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created By WuKong on 2022/8/31 16:21
 * rabbitmq 监听器
 **/
@Component
public class HospitalReceiver {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private RabbitService rabbitService;

    @RabbitListener(bindings = {
            @QueueBinding(
            value = @Queue(name = MQConst.QUEUE_ORDER,declare = "true"),
            exchange = @Exchange(name = MQConst.EXCHANGE_DIRECT_ORDER),
            key = MQConst.ROUTING_ORDER)
    })
    public void consumer(OrderMqVo orderMqVo, Message message, Channel channel){
        String scheduleId = orderMqVo.getScheduleId();
        Schedule schedule = scheduleService.getScheduleInfoByscheduleId(scheduleId);
        schedule.setAvailableNumber(orderMqVo.getAvailableNumber());
        schedule.setReservedNumber(orderMqVo.getReservedNumber());

        scheduleService.update(schedule);
        //发送短信
        MsmVo msmVo = orderMqVo.getMsmVo();
        if(null != msmVo) {
            rabbitService.sendMessage(MQConst.EXCHANGE_DIRECT_MSM, MQConst.ROUTING_MSM_ITEM, msmVo);
        }
    }
}
