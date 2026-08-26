package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.order.Order;
import com.globaltrade.logistics.core.entity.order.OrderStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class AdminOrderRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Order> findOrders(
            String search,
            OrderStatus status,
            String sortBy,
            String direction,
            int page,
            int size
    ) {
        StringBuilder jpql = new StringBuilder("SELECT o FROM Order o JOIN o.customer c " +
                "WHERE 1 = 1");

        Map<String, Object> parameters = new HashMap<>();
        if (search != null && !search.isBlank()) {
            jpql.append(" AND LOWER(o.orderNumber) LIKE :search " +
                    "OR LOWER(c.firstName) LIKE :search " +
                    "OR LOWER(c.lastName) LIKE :search " +
                    "OR LOWER(c.lastName) LIKE :search");

            parameters.put("search", "%" + search.trim().toLowerCase() + "%");
        }

        if (status != null) {
            jpql.append(" AND o.orderStatus = :status ");
            parameters.put("status", status);
        }

        String sortField = switch (sortBy == null ? "createdAt" : sortBy) {
            case "orderNumber" -> "o.orderNumber";
            case "totalAmount" -> "o.totalAmount";
            case "status" -> "o.orderStatus";
            case "createdAt" -> "o.createdAt";
            default -> "o.createdAt";
        };

        String sortDirection = "ASC".equalsIgnoreCase(direction) ? "ASC" : "DESC";

        jpql.append(" ORDER BY ")
                .append(sortField)
                .append(" ")
                .append(sortDirection);

        TypedQuery<Order> query = entityManager.createQuery(jpql.toString(), Order.class);

        parameters.forEach(query::setParameter);

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    public long countOrders(String search, OrderStatus status) {
        StringBuilder jpql = new StringBuilder("SELECT COUNT(o) FROM Order o JOIN o.customer c WHERE 1 = 1 ");

        Map<String, Object> parameters = new HashMap<>();

        if (search != null && !search.isBlank()) {
            jpql.append(" AND LOWER(o.orderNumber) LIKE :search " +
                    "OR LOWER(c.firstName) LIKE :search " +
                    "OR LOWER(c.lastName) LIKE :search " +
                    "OR LOWER(c.email) LIKE :search ");

            parameters.put("search", "%" + search.trim().toLowerCase() + "%");
        }


        if (status != null) {
            jpql.append(" AND o.orderStatus = :status ");
            parameters.put("status", status);
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql.toString(), Long.class);

        parameters.forEach(query::setParameter);
        return query.getSingleResult();
    }
}
