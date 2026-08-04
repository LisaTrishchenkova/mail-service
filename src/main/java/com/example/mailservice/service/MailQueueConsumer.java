package com.example.mailservice.service;

import com.example.mailservice.config.RabbitConfig;
import com.example.mailservice.dto.request.SendCodeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MailQueueConsumer {
    private static final Logger log = LoggerFactory.getLogger(MailQueueConsumer.class);
    private final MailService mailService;
    public MailQueueConsumer(MailService mailService) {
        this.mailService = mailService;
    }
    @RabbitListener(queues = RabbitConfig.MAIL_QUEUE)
    public void handleSendCode(SendCodeMessage message){
        log.info("Получено сообщение из очереди: to={}, purpose={}", message.to(), message.purpose());

        mailService.sendCode(message.to(), message.purpose());

        log.info("Письмо с кодом отправлено на {}", message.to());
    }
}
