package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.order.shipment.tracking.ShipmentTracking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ShipmentTrackingRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public void save(ShipmentTracking tracking) {
        entityManager.persist(tracking);
    }

    public Optional<ShipmentTracking> findLatestByShipmentId(UUID shipmentId) {

        List<ShipmentTracking> result = entityManager.createQuery("SELECT st FROM ShipmentTracking st " +
                        "WHERE st.shipment.id = :shipmentId ORDER BY st.trackingTimestamp DESC", ShipmentTracking.class)
                .setParameter("shipmentId", shipmentId)
                .setMaxResults(1)
                .getResultList();

        return result.stream().findFirst();
    }

    public List<ShipmentTracking> findByShipmentIdOrderByTrackingTimestampAsc(UUID shipmentId) {

        return entityManager.createQuery("""
                SELECT st
                FROM ShipmentTracking st
                WHERE st.shipment.id = :shipmentId
                ORDER BY st.trackingTimestamp ASC
                """, ShipmentTracking.class)
                .setParameter("shipmentId", shipmentId)
                .getResultList();
    }
}
