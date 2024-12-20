package com.swiftpos.swiftposauth.service;

import java.time.LocalDateTime;

public interface ITokenBlacklistService {
    void blacklistToken(String token, LocalDateTime expiration);

    void cleanUpTokens();

    Boolean isTokenBlacklisted(String token);
}
