package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.customer.Customer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CustomerRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public void save(Customer customer) {
        entityManager.persist(customer);
        entityManager.flush();
    }

    public Optional<Customer> findById(UUID id) {
        try {
            Customer customer = entityManager.find(Customer.class, id);
            return Optional.ofNullable(customer);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public  Optional<Customer> findByUserId(UUID userId) {
        try{
            return Optional.of(entityManager.createQuery("SELECT c FROM Customer c JOIN FETCH c.user " +
                            "JOIN FETCH c.company WHERE c.user.id =:userId",Customer.class)
                    .setParameter("userId", userId)
                    .getSingleResult());
        }catch (NoResultException e){
            return Optional.empty();
        }
    }

    public boolean existsByUserId(UUID userId) {
        Long count = entityManager.createQuery("SELECT COUNT(c) FROM Customer c WHERE c.user.id=:userId", Long.class)
                .setParameter("userId", userId)
                .getSingleResult();

        return count > 0;
    }
}
