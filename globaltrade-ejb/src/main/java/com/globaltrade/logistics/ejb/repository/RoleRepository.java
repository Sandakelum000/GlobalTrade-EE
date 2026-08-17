package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.security.Role;
import com.globaltrade.logistics.core.entity.security.RoleType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class RoleRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Role> findByName(RoleType role) {
        try{
            return Optional.of(entityManager.createNamedQuery("Role.findByName", Role.class)
                    .setParameter("role", role)
                    .getSingleResult());
        }catch (NoResultException e){
            return Optional.empty();
        }
    }
}
