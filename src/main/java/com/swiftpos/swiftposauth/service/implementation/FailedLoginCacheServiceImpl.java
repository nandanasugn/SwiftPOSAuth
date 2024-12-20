package com.swiftpos.swiftposauth.service.implementation;

import com.swiftpos.swiftposauth.service.ICacheService;
import com.swiftpos.swiftposauth.service.IFailedLoginCacheService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class FailedLoginCacheServiceImpl implements IFailedLoginCacheService {
    private final ICacheService cacheService;
    private static final long LOCKOUT_TTL = 300;

    @Override
    public void incrementFailedAttempts(String username) {
        String cacheKey = "login:failed:" + username;
        Integer attempts = (Integer) cacheService.get(cacheKey);

        if (attempts == null) {
            cacheService.set(cacheKey,1, LOCKOUT_TTL);
        } else {
            cacheService.set(cacheKey, attempts + 1, LOCKOUT_TTL);
        }
    }

    @Override
    public int getFailedAttempts(String username) {
        String cacheKey = "login:failed:" + username;
        Integer attempts = (Integer) cacheService.get(cacheKey);

        return attempts == null ? 0 : attempts;
    }

    @Override
    public void resetFailedAttempts(String username) {
        String cacheKey = "login:failed:" + username;
        cacheService.delete(cacheKey);
    }

    @Override
    public boolean isAccountLocked(String username) {
        return getFailedAttempts(username) >= 5;
    }
}
