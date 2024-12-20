package com.swiftpos.swiftposauth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret.key}")
    private String SECRET_KEY;

    @Value("${jwt.refresh.secret.key}")
    private String REFRESH_SECRET_KEY;

    @Value("${jwt.access.token.expiration}")
    private long ACCESS_TOKEN_EXPIRATION;

    @Value("${jwt.refresh.token.expiration}")
    private long REFRESH_TOKEN_EXPIRATION;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    private SecretKey getRefreshSigningKey() {
        return Keys.hmacShaKeyFor(REFRESH_SECRET_KEY.getBytes());
    }

    public String generateAccessToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        return createToken(claims, username, ACCESS_TOKEN_EXPIRATION, getSigningKey());
    }

    public String generateRefreshToken(String username) {
        return createToken(new HashMap<>(), username, REFRESH_TOKEN_EXPIRATION, getRefreshSigningKey());
    }

    private String createToken(Map<String, Object> claims, String subject, Long expiration, SecretKey signingKey) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public Boolean isTokenValid(String token, String username, Boolean isRefreshToken) {
        String tokenUsername = extractUsername(token, isRefreshToken);

        return tokenUsername.equals(username) && !isTokenExpired(token, isRefreshToken);
    }

    public String extractUsername(String token, Boolean isRefreshToken) {
        return extractClaim(token, Claims::getSubject, isRefreshToken);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, Boolean isRefreshToken) {
        Claims claims = extractAllClaims(token, isRefreshToken);

        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token, Boolean isRefreshToken) {
        SecretKey secretKey = isRefreshToken ? getRefreshSigningKey() : getSigningKey();

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token, Boolean isRefreshToken) {
        return extractClaim(token, Claims::getExpiration, isRefreshToken).before(new Date(System.currentTimeMillis()));
    }
}
