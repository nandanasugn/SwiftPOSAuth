package com.swiftpos.swiftposauth.repository;

import com.swiftpos.swiftposauth.model.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ITokenBlacklistRepository extends JpaRepository<TokenBlacklist, UUID> {
    Optional<TokenBlacklist> findByToken(String token);
}
