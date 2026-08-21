package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.order.shipment.tracking.ShipmentTracking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class ShipmentTrackingRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public void save(ShipmentTracking tracking) {
        entityManager.persist(tracking);
    }
}
