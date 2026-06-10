package com.rms.reporting.config.queue;

import com.rms.reporting.utils.constants.QueueConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserNotificationRabbitMQConfig {

    @Bean
    public TopicExchange notificationExchange() {
        return ExchangeBuilder
                .topicExchange(QueueConstants.UserNotification.EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue notificationQueue() {
        return QueueBuilder
                .durable(QueueConstants.UserNotification.Queue.MAIN)
                .withArgument("x-dead-letter-exchange", QueueConstants.UserNotification.EXCHANGE)
                .withArgument("x-dead-letter-routing-key", QueueConstants.UserNotification.RoutingKey.DLQ)
                .build();
    }

    @Bean
    public Queue notificationDLQ() {
        return QueueBuilder
                .durable(QueueConstants.UserNotification.Queue.DLQ)
                .build();
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder
                .bind(notificationQueue())
                .to(notificationExchange())
                .with(QueueConstants.UserNotification.RoutingKey.MAIN);
    }

    @Bean
    public Binding notificationDLQBinding() {
        return BindingBuilder
                .bind(notificationDLQ())
                .to(notificationExchange())
                .with(QueueConstants.UserNotification.RoutingKey.DLQ);
    }
}
