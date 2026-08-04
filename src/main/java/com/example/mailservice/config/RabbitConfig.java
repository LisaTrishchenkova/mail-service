package com.example.mailservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;



@Configuration
public class RabbitConfig {

    //Имя очереди, куда будут отправляться письма.
    public static final String MAIL_QUEUE = "mail.send.queue";

    //Имя обменника
    public static final String MAIL_EXCHANGE = "mail.exchange";

    //ключ маршрутизации
    public static final String MAIL_ROUTING_KEY = "mail.send";
    @Bean
    public Queue mailQueue(){
        return new Queue(MAIL_QUEUE, true);
    }
    @Bean
    public DirectExchange mailExchange(){
        return new DirectExchange(MAIL_EXCHANGE);
    }
    @Bean
    public Binding mailBinding(Queue mailQueue, DirectExchange mailExchange){
        return BindingBuilder
                .bind(mailQueue)
                .to(mailExchange)
                .with(MAIL_ROUTING_KEY);
    }
    @Bean
    public MessageConverter jsonMessageConverter(){
        return new JacksonJsonMessageConverter();
    }
}
