package com.swiftpos.swiftposauth.service;

import com.swiftpos.swiftposauth.dto.request.LoginRequest;
import com.swiftpos.swiftposauth.dto.request.RegisterRequest;
import com.swiftpos.swiftposauth.dto.response.LoginResponse;

import java.util.Map;

public interface IAuthService {
    LoginResponse login(LoginRequest loginRequest);

    void register(RegisterRequest registerRequest);

    void logout(String authorizationHeader);

    Map<String, String> refreshToken(Map<String, String> request);
}
