package com.swiftpos.swiftposauth.util;

import com.swiftpos.swiftposauth.dto.pojo.AuthEvent;
import com.swiftpos.swiftposauth.dto.request.UserDetailRequest;
import com.swiftpos.swiftposauth.dto.request.UserRequest;
import com.swiftpos.swiftposauth.model.User;
import com.swiftpos.swiftposauth.service.IRabbitMQPublisherService;
import com.swiftpos.swiftposauth.service.IUserDetailService;
import com.swiftpos.swiftposauth.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DefaultOwnerAccountInitializer implements CommandLineRunner {

    private final IUserService userService;
    private final IUserDetailService userDetailService;
    private final IRabbitMQPublisherService rabbitMQPublisherService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userService.getOwnerByUsername("swiftpos") == null) {
            UserRequest userRequest = UserRequest.builder()
                    .username("swiftpos")
                    .password(passwordEncoder.encode("SwiftPOS123"))
                    .role("OWNER")
                    .build();
            User user = userService.create(userRequest);

            Resource defaultFile = new ClassPathResource("static/default.jpg");

            MultipartFile multipartFile = new ResourceMultipartFile(defaultFile, "default.jpg");

            UserDetailRequest userDetailRequest = UserDetailRequest.builder()
                    .fullName("SwiftPOS Owner")
                    .email("owner@swiftpos.com")
                    .address("Owner City")
                    .phone("089561231231")
                    .birthday(LocalDate.parse("2024-12-12"))
                    .userId(user.getId())
                    .file(multipartFile)
                    .build();
            userDetailService.create(userDetailRequest);

            user = userService.getUserByUsername(user.getUsername());

            if (user.getUserDetail() == null) {
                throw new IllegalStateException("UserDetail was not associated with the User.");
            }

            AuthEvent event = MapperUtil.mapToAuthEvent("REGISTER OWNER ACCOUNT", user);
            rabbitMQPublisherService.publishEvent(event);
        }
    }
}
