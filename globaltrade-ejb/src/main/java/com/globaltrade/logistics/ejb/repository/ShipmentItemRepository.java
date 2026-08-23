package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.order.shipment.Shipment;
import com.globaltrade.logistics.core.entity.order.shipment.ShipmentItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ShipmentItemRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public void save(ShipmentItem shipmentItem) {
        entityManager.persist(shipmentItem);
    }

    public List<ShipmentItem> findByShipmentId(UUID shipmentId) {
        return entityManager.createQuery("SELECT si FROM ShipmentItem si " +
                "JOIN FETCH si.orderItem oi " +
                "JOIN FETCH oi.inventory " +
                "WHERE si.shipment.id=:shipmentId",ShipmentItem.class)
                .setParameter("shipmentId", shipmentId)
                .getResultList();
    }
}
