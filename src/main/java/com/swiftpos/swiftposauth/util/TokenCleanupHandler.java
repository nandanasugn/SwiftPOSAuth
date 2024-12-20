package com.swiftpos.swiftposauth.util;

import com.swiftpos.swiftposauth.service.ITokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenCleanupHandler {
    private final ITokenBlacklistService tokenBlacklistService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredTokens() {
        tokenBlacklistService.cleanUpTokens();
    }
}
