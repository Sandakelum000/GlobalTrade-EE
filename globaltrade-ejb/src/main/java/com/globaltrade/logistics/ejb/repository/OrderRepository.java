package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.dto.customer.dashboard.MonthlyOrderResponse;
import com.globaltrade.logistics.core.dto.customer.dashboard.OrderStatusCountResponse;
import com.globaltrade.logistics.core.dto.customer.dashboard.RecentOrderResponse;
import com.globaltrade.logistics.core.entity.order.Order;
import com.globaltrade.logistics.core.entity.order.OrderStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public long countByCustomerId(UUID customerId) {
        try {
            return entityManager.createQuery("SELECT COUNT(o) FROM Order o WHERE o.customer.id=:customerId", Long.class)
                    .setParameter("customerId", customerId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0;
        }
    }

    public long countByCustomerIdAndStatus(UUID customerId, OrderStatus status) {
        try {
            return entityManager.createQuery("SELECT COUNT(o) FROM Order o " +
                            "WHERE o.customer.id=:customerId AND " + "o.orderStatus=:status", Long.class)
                    .setParameter("customerId", customerId)
                    .setParameter("status", status)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0;
        }
    }

    public List<Order> findRecentOrdersByCustomer(UUID customerId, int limit) {
        return entityManager.createQuery("SELECT o FROM Order o WHERE o.customer.id = :customerId ORDER BY o.createdAt DESC", Order.class)
                .setParameter("customerId", customerId)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<OrderStatusCountResponse> countCustomerOrdersByStatus(UUID customerId) {

        List<Object[]> rows = entityManager.createQuery("SELECT o.orderStatus, COUNT(o) FROM Order o " +
                        "WHERE o.customer.id = :customerId GROUP BY o.orderStatus", Object[].class)
                .setParameter("customerId", customerId)
                .getResultList();

        return rows.stream()
                .map(row -> new OrderStatusCountResponse(
                        (OrderStatus) row[0],
                        ((Number) row[1]).longValue()
                ))
                .toList();
    }


    public long countAll() {
        try {
            return entityManager.createQuery("SELECT COUNT(o) FROM Order o", Long.class)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0;
        }
    }

    public long countByStatus(OrderStatus status) {
        return entityManager.createQuery("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = :status", Long.class)
                .setParameter("status", status)
                .getSingleResult();
    }

    public List<OrderStatusCountResponse> countOrdersByStatus() {
        return entityManager.createQuery("SELECT o.orderStatus, COUNT(o) " +
                        "FROM Order o GROUP BY o.orderStatus ORDER BY o.orderStatus", Object[].class)
                .getResultList()
                .stream()
                .map(row -> new OrderStatusCountResponse(
                        (OrderStatus) row[0],
                        (Long) row[1]
                ))
                .toList();
    }

    public List<Order> findExpiredUnpaidOrders(LocalDateTime expiryTime){
        return entityManager.createQuery("SELECT o FROM Order o WHERE o.orderStatus =:status " +
                "AND o.orderDate <=:expiryTime", Order.class)
                .setParameter("status",OrderStatus.PENDING)
                .setParameter("expiryTime",expiryTime)
                .getResultList();
    }


    public List<RecentOrderResponse> findRecentOrders(int limit) {
        return entityManager.createQuery("SELECT o FROM Order o " +
                        "ORDER BY o.createdAt DESC", Order.class)
                .setMaxResults(limit)
                .getResultList()
                .stream()
                .map(o -> new RecentOrderResponse(
                        o.getId(),
                        o.getOrderNumber(),
                        o.getCreatedAt(),
                        o.getTotalAmount(),
                        o.getOrderStatus()
                ))
                .toList();
    }

    public List<MonthlyOrderResponse> countMonthlyOrders() {
        List<Object[]> rows =
                entityManager.createNativeQuery(
                                """
                                SELECT
                                    DATE_FORMAT(created_at, '%Y-%m') AS month,
                                    COUNT(*) AS total
                                FROM orders
                                GROUP BY DATE_FORMAT(created_at, '%Y-%m')
                                ORDER BY month
                                """
                        )
                        .getResultList();

        return rows.stream()
                .map(row -> new MonthlyOrderResponse(
                        (String) row[0],
                        ((Number) row[1]).longValue()
                ))
                .toList();
    }


    public List<MonthlyOrderResponse> countMonthlyOrders(UUID customerId) {
        List<Object[]> rows = entityManager.createNativeQuery("""
                        SELECT
                            DATE_FORMAT(created_at, '%Y-%m') AS month,
                            COUNT(*) AS total
                        FROM orders
                        WHERE customer_id = ?
                        GROUP BY DATE_FORMAT(created_at, '%Y-%m')
                        ORDER BY month
                        """)
                .setParameter(1, customerId)
                .getResultList();

        return rows.stream()
                .map(row -> new MonthlyOrderResponse(
                        (String) row[0],
                        ((Number) row[1]).longValue()
                ))
                .collect(Collectors.toList());
    }
}
