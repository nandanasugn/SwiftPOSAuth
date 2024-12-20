package com.swiftpos.swiftposauth.util;

import com.swiftpos.swiftposauth.dto.pojo.AuthEvent;
import com.swiftpos.swiftposauth.dto.request.RegisterRequest;
import com.swiftpos.swiftposauth.dto.request.UserDetailRequest;
import com.swiftpos.swiftposauth.dto.request.UserRequest;
import com.swiftpos.swiftposauth.dto.response.CommonResponse;
import com.swiftpos.swiftposauth.dto.response.ErrorResponse;
import com.swiftpos.swiftposauth.dto.response.LoginResponse;
import com.swiftpos.swiftposauth.model.User;
import com.swiftpos.swiftposauth.model.UserDetail;
import com.swiftpos.swiftposauth.model.enums.ERole;

import java.time.LocalDateTime;
import java.util.UUID;

public class MapperUtil {
    public static CommonResponse<?> mapToCommonResponse(int status, String message, Object data) {
        return CommonResponse.builder()
                .status(status)
                .message(message)
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    public static ErrorResponse mapToErrorResponse(int status, String message, LocalDateTime timestamp, Object details) {
        return ErrorResponse.builder()
                .statusCode(status)
                .message(message)
                .timestamp(timestamp)
                .details(details)
                .build();
    }

    public static AuthEvent mapToAuthEvent(String action, User user) {
        return AuthEvent.builder()
                .action(action)
                .timestamp(String.valueOf(LocalDateTime.now()))
                .userId(String.valueOf(user.getId()))
                .role(user.getRole().name())
                .userName(user.getUsername())
                .userDetailId(String.valueOf(user.getUserDetail().getId()))
                .build();
    }

    public static User mapToUser(UserRequest userRequest) {
        return User.builder()
                .username(userRequest.getUsername())
                .password(userRequest.getPassword())
                .role(ERole.valueOf(userRequest.getRole()))
                .build();
    }

    public static UserRequest mapToUserRequestFromRegister(RegisterRequest registerRequest) {
        return UserRequest.builder()
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword())
                .role(registerRequest.getRole())
                .build();
    }

    public static UserDetail mapToUserDetail(UserDetailRequest userDetailRequest) {
        return UserDetail.builder()
                .fullName(userDetailRequest.getFullName())
                .email(userDetailRequest.getEmail())
                .address(userDetailRequest.getAddress())
                .phone(userDetailRequest.getPhone())
                .dateOfBirth(userDetailRequest.getBirthday())
                .build();
    }

    public static UserDetailRequest mapToUserDetailRequest(RegisterRequest registerRequest) {
        return UserDetailRequest.builder()
                .fullName(registerRequest.getFullName())
                .email(registerRequest.getEmail())
                .address(registerRequest.getAddress())
                .phone(registerRequest.getPhone())
                .birthday(registerRequest.getBirthday())
                .file(registerRequest.getImage())
                .build();
    }

    public static LoginResponse mapToLoginResponse(UUID userId, String accessToken, String refreshToken) {
        return LoginResponse.builder()
                .userId(userId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
