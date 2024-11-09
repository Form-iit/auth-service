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

    @Autowired
    public MailsProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendEmail(String to, String subject, String content) {
        try {
            EmailVerificationRequest emailVerificationRequest = EmailVerificationRequest.builder()
                    .to(to)
                    .subject(subject)
                    .content(content)
                    .build();
            logger.info("Sending email to {} with subject {}", to, subject);
            rabbitTemplate.convertAndSend(RabbitConfig.ExchangeName, RabbitConfig.ROUTING_KEY, emailVerificationRequest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
