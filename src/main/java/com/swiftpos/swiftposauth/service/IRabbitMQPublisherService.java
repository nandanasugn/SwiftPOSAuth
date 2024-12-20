package com.swiftpos.swiftposauth.service;

import com.swiftpos.swiftposauth.dto.pojo.AuthEvent;

public interface IRabbitMQPublisherService {
    void publishEvent(AuthEvent event);
}
