package com.swiftpos.swiftposauth.service.implementation;

import com.swiftpos.swiftposauth.service.ICacheService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Transactional
@RequiredArgsConstructor
public class CacheServiceImpl implements ICacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void set(String key, Object value, long ttlInSeconds) {
        redisTemplate.opsForValue().set(key, value, ttlInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }
}
