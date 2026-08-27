package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.dto.admin.dashboard.AdminShipmentResponse;
import com.globaltrade.logistics.core.dto.customer.dashboard.ShipmentStatusCountResponse;
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

    public Optional<Shipment> findByShipmentIdWithItems(UUID shipmentId) {
        return entityManager.createQuery("SELECT DISTINCT s FROM Shipment s " +
                "LEFT JOIN FETCH s.items si " +
                "LEFT JOIN FETCH si.orderItem oi " +
                "LEFT JOIN FETCH oi.inventory i " +
                "LEFT JOIN FETCH i.product p " +
                "WHERE s.id=:shipmentId", Shipment.class)
                .setParameter("shipmentId", shipmentId)
                .getResultList().stream().findFirst();
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

    public long countActiveShipmentsByCustomerId(UUID customerId) {
        try {
            return entityManager.createQuery("SELECT COUNT(s) FROM Shipment s WHERE s.order.customer.id=:customerId AND s.status NOT IN (:delivered,:cancelled)", Long.class)
                    .setParameter("customerId",customerId)
                    .setParameter("delivered", ShipmentStatus.DELIVERED)
                    .setParameter("cancelled", ShipmentStatus.CANCELLED)
                    .getSingleResult();
        }catch (NoResultException e){
            return 0;
        }
    }

    public long countByCustomerIdAndStatus(UUID customerId, ShipmentStatus status) {
        try {
            return entityManager.createQuery("SELECT COUNT(s) FROM Shipment s " +
                    "WHERE s.order.customer.id=:customerId AND s.status=:status", Long.class)
                    .setParameter("customerId",customerId)
                    .setParameter("status",status)
                    .getSingleResult();
        }catch (NoResultException e){
            return 0;
        }
    }

    public List<Shipment> findRecentShipmentsByCustomer(UUID customerId, int limit) {

        return entityManager.createQuery("SELECT s FROM Shipment s WHERE s.order.customer.id = :customerId " +
                        "ORDER BY s.createdAt DESC ", Shipment.class)
                .setParameter("customerId", customerId)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<ShipmentStatusCountResponse> countShipmentsByStatus(UUID customerId) {

        List<Object[]> rows = entityManager.createQuery("""
            SELECT s.status, COUNT(s)
            FROM Shipment s
            WHERE s.order.customer.id = :customerId
            GROUP BY s.status
            """, Object[].class)
                .setParameter("customerId", customerId)
                .getResultList();

        return rows.stream()
                .map(row -> new ShipmentStatusCountResponse(
                        (ShipmentStatus) row[0],
                        ((Number) row[1]).longValue()
                ))
                .toList();
    }

    //admin
    public long countActiveShipments() {
        return entityManager.createQuery("SELECT COUNT(s) FROM Shipment s WHERE s.status " +
                        "NOT IN (:delivered, :cancelled)", Long.class)
                .setParameter("delivered", ShipmentStatus.DELIVERED)
                .setParameter("cancelled", ShipmentStatus.CANCELLED)
                .getSingleResult();
    }

    public long countByStatus(ShipmentStatus status) {
        return entityManager.createQuery("SELECT COUNT(s) FROM Shipment s WHERE s.status = :status", Long.class)
                .setParameter("status", status)
                .getSingleResult();
    }

    public List<ShipmentStatusCountResponse> countShipmentsByStatus() {
        return entityManager.createQuery("SELECT s.status, COUNT(s) FROM Shipment s GROUP BY s.status ORDER BY s.status", Object[].class)
                .getResultList()
                .stream()
                .map(row -> new ShipmentStatusCountResponse(
                        (ShipmentStatus) row[0],
                        (Long) row[1]
                ))
                .toList();
    }

    public List<AdminShipmentResponse> findActiveShipmentRecords(int limit) {

        return entityManager.createQuery("SELECT s FROM Shipment s WHERE s.status NOT IN (:delivered, :cancelled) ORDER BY s.createdAt DESC", Shipment.class)
                .setParameter("delivered", ShipmentStatus.DELIVERED)
                .setParameter("cancelled", ShipmentStatus.CANCELLED)
                .setMaxResults(limit)
                .getResultList()
                .stream()
                .map(s -> new AdminShipmentResponse(
                        s.getId(),
                        s.getShipmentNumber(),
                        s.getOrder().getId(),
                        s.getOrder().getOrderNumber(),
                        s.getWarehouse().getId(),
                        s.getWarehouse().getName(),
                        s.getStatus(),
                        s.getShippedAt(),
                        s.getEstimatedDeliveryDate(),
                        s.getDeliveredAt()
                ))
                .toList();
    }


}
