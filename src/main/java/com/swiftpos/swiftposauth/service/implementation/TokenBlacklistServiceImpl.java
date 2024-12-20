package com.swiftpos.swiftposauth.service.implementation;

import com.swiftpos.swiftposauth.model.TokenBlacklist;
import com.swiftpos.swiftposauth.repository.ITokenBlacklistRepository;
import com.swiftpos.swiftposauth.service.ITokenBlacklistService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements ITokenBlacklistService {
    private final ITokenBlacklistRepository tokenBlacklistRepository;

    @Override
    public void blacklistToken(String token, LocalDateTime expiration) {
        TokenBlacklist tokenBlacklist = TokenBlacklist.builder()
                .token(token)
                .expiration(expiration)
                .build();
        tokenBlacklistRepository.save(tokenBlacklist);
    }

    @Override
    public void cleanUpTokens() {
        tokenBlacklistRepository.deleteAll(
                tokenBlacklistRepository.findAll().stream()
                        .filter(token -> token.getExpiration().isBefore(LocalDateTime.now()))
                        .toList()
        );
    }

    @Override
    public Boolean isTokenBlacklisted(String token) {
        return tokenBlacklistRepository.findByToken(token).isPresent();
    }
}
