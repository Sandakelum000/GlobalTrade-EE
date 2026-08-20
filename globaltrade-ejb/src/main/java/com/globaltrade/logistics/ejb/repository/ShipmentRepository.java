package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.order.shipment.Shipment;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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

}
