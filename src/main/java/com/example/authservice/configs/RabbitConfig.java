package com.example.authservice.configs;

import lombok.Getter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RabbitConfig {
  public final String queueName;
  public final String exchangeName;
  public final String routingKey;

  public RabbitConfig(
      @Value("${spring.rabbitmq.exchange.name}") String ExchangeName,
      @Value("${spring.rabbitmq.routing.key}") String routingKey,
      @Value("${spring.rabbitmq.queue.name}") String QueueName) {
    this.exchangeName = ExchangeName;
    this.queueName = QueueName;
    this.routingKey = routingKey;
  }

  @Bean
  Queue EmailsQueue() {
    return new Queue(queueName, false);
  }

  @Bean
  DirectExchange emailExchange() {
    return new DirectExchange(exchangeName);
  }

  @Bean
  Binding binding() {
    return BindingBuilder.bind(EmailsQueue()).to(emailExchange()).with(routingKey);
  }

  @Bean
  RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
    return rabbitTemplate;
  }

  @Bean
  Jackson2JsonMessageConverter producerJackson2MessageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
