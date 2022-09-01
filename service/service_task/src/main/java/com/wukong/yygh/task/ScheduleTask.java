package com.wukong.yygh.task;

import com.wukong.yygh.rabbit.MQConst;
import com.wukong.yygh.rabbit.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.AccessType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created By WuKong on 2022/9/1 16:43
 * 定时任务
 **/
@Component
public class ScheduleTask {

    @Autowired
    private RabbitService rabbitService;

    //Quartz: cron表达式: 秒 分 时 dayOfMonth Month dayOfWeek year
    @Scheduled(cron = "0/30 * * * * ?")
    public void printTime(){
        String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        System.out.println("当前时间："+format);
        rabbitService.sendMessage(MQConst.EXCHANGE_DIRECT_TASK,MQConst.ROUTING_TASK_8,"XX");
    }
}
