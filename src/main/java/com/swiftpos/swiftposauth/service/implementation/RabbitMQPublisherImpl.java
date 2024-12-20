package com.swiftpos.swiftposauth.service.implementation;

import com.swiftpos.swiftposauth.config.RabbitMQConfig;
import com.swiftpos.swiftposauth.dto.pojo.AuthEvent;
import com.swiftpos.swiftposauth.service.IRabbitMQPublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMQPublisherImpl implements IRabbitMQPublisherService {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishEvent(AuthEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.AUTH_ROUTING_KEY, event);
        System.out.println("Published event: " + event);
    }
}
