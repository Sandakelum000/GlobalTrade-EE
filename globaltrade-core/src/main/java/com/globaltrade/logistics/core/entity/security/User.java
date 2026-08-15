package com.globaltrade.logistics.core.entity.security;

import com.globaltrade.logistics.core.entity.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Builder
@Entity
@Table(name = "users")
@NamedQueries({
        @NamedQuery(name = "User.findByUsername",query = "SELECT u FROM User u WHERE u.username=:username"),
        @NamedQuery(name = "User.findByEmail",query = "SELECT u FROM User u WHERE u.email=:email"),
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    @NotBlank
    @Column(name = "username", nullable = false, unique = true,length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Email
    @NotBlank
    @Column(name = "email", nullable = false, unique = true,length = 150)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
        if (role == null) return;
        roles.add(role);
        role.getUsers().add(this);
    }

    public void removeRole(Role role) {
        if (role == null) return;
        roles.remove(role);
        role.getUsers().remove(this);
    }
}
