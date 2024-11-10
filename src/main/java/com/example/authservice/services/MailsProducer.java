package com.example.authservice.services;

import com.example.authservice.configs.RabbitConfig;
import com.example.authservice.mailTemplates.dto.EmailVerificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MailsProducer {
    private static final Logger logger = LoggerFactory.getLogger(MailsProducer.class);
    private final RabbitTemplate rabbitTemplate;
    private final RabbitConfig rabbitConfig;

    @Autowired
    public MailsProducer(RabbitTemplate rabbitTemplate, RabbitConfig rabbitConfig) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitConfig = rabbitConfig;
    }

    public void sendEmail(String to, String subject, String content) {
        try {
            EmailVerificationRequest emailVerificationRequest = EmailVerificationRequest.builder()
                    .to(to)
                    .subject(subject)
                    .content(content)
                    .build();
            logger.info("Sending email to {} with subject {}", to, subject);
            rabbitTemplate.convertAndSend(rabbitConfig.getExchangeName(), rabbitConfig.getRoutingKey(), emailVerificationRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
