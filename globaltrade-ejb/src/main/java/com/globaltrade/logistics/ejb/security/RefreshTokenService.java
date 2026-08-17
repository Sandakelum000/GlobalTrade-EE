package com.globaltrade.logistics.ejb.security;

import com.globaltrade.logistics.core.entity.security.RefreshToken;
import com.globaltrade.logistics.core.entity.security.User;
import com.globaltrade.logistics.ejb.repository.RefreshTokenRepository;
import com.globaltrade.logistics.ejb.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@ApplicationScoped
@Transactional
public class RefreshTokenService {
    private static final int TOKEN_BYTES = 32;
    private static final long REFRESH_TOKEN_EXPIRATION_SECONDS = 30L * 24 * 60 * 60;;
    private final SecureRandom secureRandom = new SecureRandom();

    @Inject
    private RefreshTokenRepository refreshTokenRepository;

    @Inject
    private UserRepository userRepository;

    public record RefreshResult(User user, String refreshToken) {}


    @Transactional(Transactional.TxType.REQUIRED)
    public String createByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found: " + username)
                );
        refreshTokenRepository.deleteRefreshTokenByUserId(user.getId());
        return create(user);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public RefreshResult rotateRfToken(String rawToken){
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException("Refresh token is required");
        }
        String tokenHash = hashToken(rawToken);
        User user = refreshTokenRepository.findByTokenHash(tokenHash)
                .filter(refreshToken -> !refreshToken.isExpired())
                .map(RefreshToken::getUser)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired refresh token"));

        refreshTokenRepository.deleteRefreshToken(tokenHash);
        return new RefreshResult(user, tokenHash);
    }

    public String create(User user) {
        String rawToken = generateToken();
        RefreshToken refreshToken = RefreshToken.builder()
                .tokenHash(hashToken(rawToken))
                .user(user)
                .expiresAt(Instant.now().plusSeconds(REFRESH_TOKEN_EXPIRATION_SECONDS))
                .build();

        refreshTokenRepository.saveRefreshToken(refreshToken);
        return rawToken;
    }

    public User validate(String rawToken){
        if(rawToken == null || rawToken.isBlank()){
            return null;
        }
        String tokenHash = hashToken(rawToken);
        return refreshTokenRepository.findByTokenHash(tokenHash)
                .filter(refreshToken -> !refreshToken.isExpired())
                .map(RefreshToken::getUser)
                .orElse(null);
    }

    public void deleteRefreshToken(String tokenHash){
        refreshTokenRepository.deleteRefreshToken(tokenHash);
    }

    public int cleanup(){
        return refreshTokenRepository.deleteExpiredToken();
    }

    public String generateToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String hashToken(String token) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            return bytesToHex(hash);
        }catch (NoSuchAlgorithmException e){
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

}
