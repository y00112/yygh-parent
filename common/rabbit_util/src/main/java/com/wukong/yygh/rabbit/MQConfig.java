package com.wukong.yygh.rabbit;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created By WuKong on 2022/8/31 15:15
 **/
@Configuration
public class MQConfig {

    /**
     * 配置MessageConverter 可以往mq中发送对象
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }
}