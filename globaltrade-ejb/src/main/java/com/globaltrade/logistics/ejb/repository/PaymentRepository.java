package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.payment.Payment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PaymentRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public void save(Payment payment) {
        entityManager.persist(payment);
        entityManager.flush();
    }

    public Optional<Payment> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(Payment.class, id));
    }

    public Optional<Payment> findByOrderId(UUID orderId) {
        try{
            return Optional.of(
                    entityManager.createNamedQuery("Payment.findByOrderId", Payment.class)
                            .setParameter("orderId",orderId)
                            .getSingleResult()
            );
        }catch (NoResultException e){
            return Optional.empty();
        }
    }
}
