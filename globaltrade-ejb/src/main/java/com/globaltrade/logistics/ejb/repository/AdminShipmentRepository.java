package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.order.shipment.Shipment;
import com.globaltrade.logistics.core.entity.order.shipment.ShipmentStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class AdminShipmentRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Shipment> findShipments(
            String search,
            ShipmentStatus status,
            UUID warehouseId,
            String sortBy,
            String direction,
            int page,
            int size
    ) {

        StringBuilder jpql = new StringBuilder("SELECT s FROM Shipment s JOIN s.order o JOIN s.warehouse w WHERE 1 = 1 ");

        Map<String, Object> parameters = new HashMap<>();

        if (search != null && !search.isBlank()) {
            jpql.append("AND (LOWER(s.shipmentNumber) LIKE :search OR LOWER(o.orderNumber) " +
                    "LIKE :search OR LOWER(w.name) LIKE :search) ");

            parameters.put("search", "%" + search.trim().toLowerCase() + "%");
        }

        if (status != null) {
            jpql.append(" AND s.status = :status ");
            parameters.put("status", status);
        }

        if (warehouseId != null) {
            jpql.append(" AND w.id = :warehouseId ");
            parameters.put("warehouseId", warehouseId);
        }

        String sortField = switch (sortBy == null ? "createdAt" : sortBy) {

            case "shipmentNumber" -> "s.shipmentNumber";
            case "status" -> "s.status";
            case "shippedAt" -> "s.shippedAt";
            case "estimatedDeliveryDate" -> "s.estimatedDeliveryDate";
            default -> "s.createdAt";
        };

        String sortDirection = "ASC".equalsIgnoreCase(direction) ? "ASC" : "DESC";

        jpql.append(" ORDER BY ")
                .append(sortField)
                .append(" ")
                .append(sortDirection);

        TypedQuery<Shipment> query = entityManager.createQuery(jpql.toString(), Shipment.class);

        parameters.forEach(query::setParameter);

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    public long countShipments(String search, ShipmentStatus status, UUID warehouseId) {

        StringBuilder jpql = new StringBuilder("SELECT COUNT(s) FROM Shipment s JOIN s.order o " +
                "JOIN s.warehouse w WHERE 1 = 1 ");

        Map<String, Object> parameters = new HashMap<>();

        if (search != null && !search.isBlank()) {
            jpql.append(" AND (LOWER(s.shipmentNumber) LIKE :search OR LOWER(o.orderNumber) LIKE :search OR LOWER(w.name) LIKE :search) ");

            parameters.put("search", "%" + search.trim().toLowerCase() + "%");
        }

        if (status != null) {
            jpql.append(" AND s.status = :status ");
            parameters.put("status", status);
        }

        if (warehouseId != null) {
            jpql.append(" AND w.id = :warehouseId ");
            parameters.put("warehouseId", warehouseId);
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql.toString(), Long.class);

        parameters.forEach(query::setParameter);

        return query.getSingleResult();
    }
}
