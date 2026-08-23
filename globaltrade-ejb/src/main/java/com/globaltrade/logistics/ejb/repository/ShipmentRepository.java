package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.order.shipment.Shipment;
import com.globaltrade.logistics.core.entity.order.shipment.ShipmentStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ShipmentRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public void save(Shipment shipment) {
        entityManager.persist(shipment);
    }

    public Optional<Shipment> findById(UUID id) {
        return Optional.ofNullable(
                entityManager.find(Shipment.class, id)
        );
    }

    public List<Shipment> findByOrderId(UUID orderId) {
        return entityManager.createQuery("SELECT s FROM Shipment s WHERE s.order.id=:orderId", Shipment.class)
                .setParameter("orderId",orderId)
                .getResultList();
    }

    public boolean existsByOrderId(UUID orderId) {
        try{
            Long count = entityManager.createQuery("SELECT COUNT(s) FROM Shipment s WHERE s.order.id=:orderId", Long.class)
                    .setParameter("orderId", orderId)
                    .getSingleResult();

            return count > 0;
        }catch (NoResultException e){
            return false;
        }

    }

    public List<Shipment> findByShipmentsByOrderId(UUID orderId) {
        return entityManager.createQuery("SELECT s FROM Shipment s JOIN FETCH s.order " +
                "JOIN FETCH s.warehouse WHERE s.order.id = :orderId", Shipment.class)
                .setParameter("orderId",orderId)
                .getResultList();
    }

    public List<Shipment> findActiveShipments() {
        return entityManager.createNamedQuery("Shipment.findActiveShipments", Shipment.class)
                .setParameter("delivered", ShipmentStatus.DELIVERED)
                .setParameter("cancelled", ShipmentStatus.CANCELLED)
                .getResultList();
    }


}
