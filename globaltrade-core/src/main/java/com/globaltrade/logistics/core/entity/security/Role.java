package com.globaltrade.logistics.core.entity.security;

import com.globaltrade.logistics.core.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@NamedQueries({
        @NamedQuery(name = "Role.findByName",query = "SELECT r FROM Role r WHERE r.name=:role")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, length = 50,unique = true)
    private RoleType name;

    @ManyToMany(
            mappedBy = "roles",
            fetch = FetchType.LAZY)
    @Builder.Default
    private Set<User> users = new HashSet<>();

    public void removeUser(User user) {
        if (user == null) return;
        users.remove(user);
        user.getRoles().remove(this);
    }
}
