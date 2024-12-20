package com.swiftpos.swiftposauth.service.implementation;

import com.swiftpos.swiftposauth.dto.pojo.AuthEvent;
import com.swiftpos.swiftposauth.dto.request.LoginRequest;
import com.swiftpos.swiftposauth.dto.request.RegisterRequest;
import com.swiftpos.swiftposauth.dto.request.UserDetailRequest;
import com.swiftpos.swiftposauth.dto.request.UserRequest;
import com.swiftpos.swiftposauth.dto.response.LoginResponse;
import com.swiftpos.swiftposauth.exception.CustomException;
import com.swiftpos.swiftposauth.model.User;
import com.swiftpos.swiftposauth.service.*;
import com.swiftpos.swiftposauth.util.JwtUtil;
import com.swiftpos.swiftposauth.util.MapperUtil;
import com.swiftpos.swiftposauth.util.ObjectValidator;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    private final ObjectValidator objectValidator;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final IUserService userService;
    private final IUserDetailService userDetailService;
    private final ITokenBlacklistService tokenBlacklistService;
    private final IRabbitMQPublisherService rabbitMQPublisherService;
    private final ICacheService cacheService;
    private final IFailedLoginCacheService failedLoginCacheService;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        objectValidator.validate(loginRequest);

        if (failedLoginCacheService.isAccountLocked(loginRequest.getUsername())){
            throw new CustomException("Account is locked due too many failed login attempts.", HttpStatus.BAD_REQUEST);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = (User) authentication.getPrincipal();

            String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getRole().name());
            String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

            AuthEvent event = MapperUtil.mapToAuthEvent("LOGIN", user);
            rabbitMQPublisherService.publishEvent(event);

            failedLoginCacheService.resetFailedAttempts(loginRequest.getUsername());

            return MapperUtil.mapToLoginResponse(user.getId(), accessToken, refreshToken);
        } catch (Exception e) {
            failedLoginCacheService.incrementFailedAttempts(loginRequest.getUsername());
            throw new CustomException("Failed to login", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void register(RegisterRequest registerRequest) {
        objectValidator.validate(registerRequest);

        UserRequest userRequest = MapperUtil.mapToUserRequestFromRegister(registerRequest);
        User user = userService.create(userRequest);

        UserDetailRequest userDetailRequest = MapperUtil.mapToUserDetailRequest(registerRequest);
        userDetailRequest.setFile(registerRequest.getImage());
        userDetailRequest.setUserId(user.getId());
        userDetailService.create(userDetailRequest);

        user = userService.getUserByUsername(user.getUsername());

        if (user.getUserDetail() == null) {
            throw new IllegalStateException("UserDetail was not associated with the User.");
        }

        AuthEvent event = MapperUtil.mapToAuthEvent("REGISTER", user);
        rabbitMQPublisherService.publishEvent(event);
    }

    @Override
    public void logout(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new CustomException("Invalid token", HttpStatus.BAD_REQUEST);
        }

        String token = authorizationHeader.substring(7);

        try {
            String username = jwtUtil.extractUsername(token, false); // Assuming it's not a refresh token

            User user = userService.getUserByUsername(username);

            LocalDateTime expiration = jwtUtil.extractClaim(token, Claims::getExpiration, false).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            String sessionKey = "session:" + user.getId();
            cacheService.delete(sessionKey);

            AuthEvent event = MapperUtil.mapToAuthEvent("LOGOUT", user);
            rabbitMQPublisherService.publishEvent(event);

            tokenBlacklistService.blacklistToken(token, expiration);

        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage());
            throw new CustomException("Logout failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Map<String, String> refreshToken(Map<String, String> request) {
        if (request == null) {
            throw new CustomException("request is null", HttpStatus.BAD_REQUEST);
        }

        String refreshToken = request.get("refreshToken");
        if (refreshToken == null) {
            throw new CustomException("Failed to refresh access token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.extractUsername(refreshToken, true);

        if (!jwtUtil.isTokenValid(refreshToken, username, true)) {
            throw new CustomException("Invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        UserDetails userDetails = userService.loadUserByUsername(username);
        User user = userService.getUserByUsername(userDetails.getUsername());

        String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername(), user.getRole().name());

        AuthEvent event = MapperUtil.mapToAuthEvent("REFRESH_TOKEN", user);
        rabbitMQPublisherService.publishEvent(event);

        return Map.of("access_token", accessToken);
    }
}
