package com.globaltrade.logistics.core.entity.security;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_token_hash", columnList = "token_hash", unique = true),
                @Index(name = "idx_refresh_token_expires", columnList = "expires_at"),
                @Index(name = "idx_refresh_token_user", columnList = "user_id")
        }
)
@NamedQueries({
        @NamedQuery(name = "RefreshToken.findByTokenHash",query = "SELECT r FROM RefreshToken r JOIN FETCH r.user WHERE r.tokenHash=:tokenHash"),
        @NamedQuery(name = "RefreshToken.deleteToken",query = "DELETE FROM RefreshToken t WHERE t.tokenHash=:tokenHash"),
        @NamedQuery(name = "RefreshToken.deleteExpiredTokens",query = "DELETE FROM RefreshToken t WHERE t.expiresAt<:now")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_refresh_token_user"))
    private User user;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;


    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
