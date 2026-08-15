package com.globaltrade.logistics.ejb.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.*;


@ApplicationScoped
public class JWTService {

    private static final String PRIVATE_KEY_PATH = "/META-INF/privateKey.pem";
    private static final String PUBLIC_KEY_PATH = "/META-INF/publicKey.pem";

    private Algorithm algorithm;
    private JWTVerifier verifier;

    @Inject
    @ConfigProperty(name = "jwt.issuer")
    private String issuer;

    @Inject
    @ConfigProperty(name = "jwt.access-token-expiration-seconds")
    private long accessTokenExpirationSeconds;

    @PostConstruct
    public void init() {
        System.out.println("JWT - issuer = " + issuer);
        System.out.println("JWT - expiration = " + accessTokenExpirationSeconds + " seconds");

        RSAPrivateKey privateKey = loadPrivateKey();
        RSAPublicKey publicKey = loadPublicKey();

        algorithm = Algorithm.RSA256(publicKey, privateKey);
        verifier = JWT.require(algorithm).withIssuer(issuer).build();
    }

    public String generateAccessToken(String username, Set<String> roles) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(accessTokenExpirationSeconds);

        return JWT.create()
                .withIssuer(issuer)
                .withSubject(username)
                .withClaim("roles", List.copyOf(roles))
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiration))
                .withJWTId(UUID.randomUUID().toString())
                .sign(algorithm);
    }

    public boolean isValidToken(String token) {
        if (token == null || token.isBlank()) return false;

        try {
            parseToken(token);
            return true;
        } catch (JWTVerificationException e) {
            e.printStackTrace();
            return false;
        }

    }

    public DecodedJWT decode(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("JWT token is required");
        }
        return parseToken(token);
    }

    private DecodedJWT parseToken(String token) {
        return verifier.verify(token);
    }

    public String getUsername(String token) {
        return decode(token).getSubject();
    }

    public List<String> getRoles(String token) {
        List<String> roles = decode(token)
                .getClaim("roles")
                .asList(String.class);
        return roles != null ? roles : List.of();
    }


    public RSAPrivateKey loadPrivateKey() {
        try (InputStream inputStream = this.getClass().getResourceAsStream(PRIVATE_KEY_PATH)) {
            if (inputStream == null) {
                throw new IllegalStateException("Private key not found" + PRIVATE_KEY_PATH);
            }
            String pem = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            String key = pem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] decoded = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey)
                    keyFactory.generatePrivate(keySpec);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to load RSA private key", e);
        }
    }

    private RSAPublicKey loadPublicKey() {

        try (InputStream inputStream = this.getClass().getResourceAsStream(PUBLIC_KEY_PATH)) {
            if (inputStream == null) {
                throw new IllegalStateException("Public key not found: " + PUBLIC_KEY_PATH);
            }
            String pem = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            String key = pem
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] decoded = Base64.getDecoder().decode(key);

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return (RSAPublicKey) keyFactory.generatePublic(keySpec);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to load RSA public key", e);
        }
    }
}
