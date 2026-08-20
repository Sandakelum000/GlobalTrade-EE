package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.order.Order;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class OrderRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public void save(Order order) {
        entityManager.persist(order);
        entityManager.flush();
    }

    public Optional<Order> findById(UUID orderId) {
        return Optional.ofNullable(entityManager.find(Order.class, orderId));
    }

}
