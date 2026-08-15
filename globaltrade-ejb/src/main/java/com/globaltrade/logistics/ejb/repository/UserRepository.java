package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.security.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;

@ApplicationScoped
public class UserRepository {
    @PersistenceContext
    private EntityManager em;

    public Optional<User> findByUsername(String username) {
       try{
           return Optional.of(em.createNamedQuery("User.findByUsername", User.class)
                   .setParameter("username", username).getSingleResult());
       }catch(NoResultException e){
           return Optional.empty();
       }
    }

    public Optional<User> findByEmail(String email) {
       try{
           return Optional.of(em.createNamedQuery("User.findByEmail", User.class)
                   .setParameter("email", email).getSingleResult());
       }catch(NoResultException e){
           return Optional.empty();
       }
    }

}
