package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.security.RefreshToken;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.time.Instant;
import java.util.Optional;

@ApplicationScoped
public class RefreshTokenRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public void saveRefreshToken(RefreshToken refreshToken) {
        entityManager.persist(refreshToken);
    }

    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        try {
            return Optional.of(entityManager.createNamedQuery("RefreshToken.findByTokenHash", RefreshToken.class)
                    .setParameter("tokenHash", tokenHash)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public int deleteRefreshToken(String tokenHash) {
        return entityManager.createNamedQuery("RefreshToken.deleteToken")
                .setParameter("tokenHash", tokenHash)
                .executeUpdate();
    }

    public int deleteExpiredToken(){
        return entityManager.createNamedQuery("RefreshToken.deleteExpiredTokens")
                .setParameter("now", Instant.now())
                .executeUpdate();
    }

}
