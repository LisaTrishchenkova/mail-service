package com.example.mailservice.service;

import com.example.mailservice.config.RabbitConfig;
import com.example.mailservice.dto.request.SendCodeMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class MailQueueProducer {
    private final RabbitTemplate rabbitTemplate;

    public MailQueueProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    public void sendCodeToQueue(SendCodeMessage message){
        rabbitTemplate.convertAndSend(
                RabbitConfig.MAIL_EXCHANGE,
                RabbitConfig.MAIL_ROUTING_KEY,
                message
        );
    }
}
