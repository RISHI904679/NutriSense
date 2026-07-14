package com.nutrisense.service.auth;

import com.nutrisense.entity.RefreshToken;
import com.nutrisense.entity.User;
import com.nutrisense.exception.InvalidTokenException;
import com.nutrisense.exception.TokenExpiredException;
import com.nutrisense.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final long REFRESH_TOKEN_EXPIRATION_MILLIS = 2_592_000_000L;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        removeExpiredOrRevokedTokens();

        Instant now = Instant.now();
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(generateToken())
                .expiresAt(now.plusMillis(REFRESH_TOKEN_EXPIRATION_MILLIS))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken validateRefreshToken(String token) {
        removeExpiredOrRevokedTokens();

        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (!refreshToken.getExpiresAt().isAfter(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new TokenExpiredException("Refresh token has expired");
        }

        if (Boolean.TRUE.equals(refreshToken.getRevoked())) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidTokenException("Refresh token has been revoked");
        }

        return refreshToken;
    }

    @Transactional
    public void revokeRefreshToken(String token) {
        removeExpiredOrRevokedTokens();

        refreshTokenRepository.findByToken(token).ifPresent(refreshToken -> {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
        });
    }

    @Transactional
    public void revokeUserRefreshTokens(Long userId) {
        removeExpiredOrRevokedTokens();

        refreshTokenRepository.findByUserIdAndRevokedFalse(userId).forEach(refreshToken -> {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
        });
    }

    private String generateToken() {
        byte[] tokenBytes = new byte[32];
        SECURE_RANDOM.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    private void removeExpiredOrRevokedTokens() {
        Instant now = Instant.now();
        refreshTokenRepository.findAll().stream()
                .filter(refreshToken -> Boolean.TRUE.equals(refreshToken.getRevoked())
                        || !refreshToken.getExpiresAt().isAfter(now))
                .forEach(refreshTokenRepository::delete);
    }
}
