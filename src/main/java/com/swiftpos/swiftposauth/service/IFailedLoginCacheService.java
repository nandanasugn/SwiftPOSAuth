package com.swiftpos.swiftposauth.service;

public interface IFailedLoginCacheService {
    void incrementFailedAttempts(String username);

    int getFailedAttempts(String username);

    void resetFailedAttempts(String username);

    boolean isAccountLocked(String username);
}
