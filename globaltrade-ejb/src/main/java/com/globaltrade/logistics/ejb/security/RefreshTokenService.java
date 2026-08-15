package com.globaltrade.logistics.ejb.security;

import com.globaltrade.logistics.core.entity.security.User;
import com.globaltrade.logistics.ejb.repository.RefreshTokenRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@ApplicationScoped
@Transactional
public class RefreshTokenService {
    private static final int TOKEN_BYTES = 32;
    private static final long REFRESH_TOKEN_EXPIRATION_SECONDS = 30L * 24 * 60 * 60;;
    private final SecureRandom secureRandom = new SecureRandom();

    @Inject
    private RefreshTokenRepository refreshTokenRepository;

    public String create(User user) {

    }

    @Transactional
    public User validateAndRevoke(String rawToken){
        if(rawToken == null || rawToken.isBlank()){
            return null;
        }
        hashToken(rawToken);
    }

    @Transactional
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
