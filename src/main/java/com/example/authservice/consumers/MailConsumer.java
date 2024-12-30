package com.example.authservice.consumers;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MailConsumer {
  @RabbitListener(
      queues = "registrationFeedbackQueue",
      containerFactory = "rabbitListenerContainerFactory")
  public void handleEmailFeedback(Map<String, Object> feedback) {
    log.info("{}", feedback);
    log.info(
        "Received feedback with correlationId: {} and status: {}",
        feedback.get("correlationId"),
        feedback.get("status"));
  }
}
