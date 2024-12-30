package com.example.authservice.configs.rabbitmq;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.core.Queue;
import java.util.List;

@Slf4j
@Configuration
public class QueueConfig {
    private RabbitAdmin rabbitAdmin;
    private List<String> queueNames;

    public QueueConfig(RabbitAdmin rabbitAdmin,
            @Value("#{'${spring.rabbitmq.queues}'.split(',')}") List<String> queueNames) {
        this.rabbitAdmin = rabbitAdmin;
        this.queueNames = queueNames;
    }

    @PostConstruct
    private void init() {
        queueNames.forEach(queueName -> {
            Queue queue = new Queue(queueName, true);
            rabbitAdmin.declareQueue(queue);
        });

        log.info("Queues created: {}", queueNames);
    }
}
