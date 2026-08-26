package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.dto.admin.dashboard.MonthlyRevenueResponse;
import com.globaltrade.logistics.core.dto.customer.dashboard.MonthlySpendingResponse;
import com.globaltrade.logistics.core.entity.order.shipment.tracking.ShipmentTracking;
import com.globaltrade.logistics.core.entity.payment.Payment;
import com.globaltrade.logistics.core.entity.payment.PaymentStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PaymentRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public void save(Payment payment) {
        entityManager.persist(payment);
        entityManager.flush();
    }

    public Optional<Payment> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(Payment.class, id));
    }

    public Optional<Payment> findByOrderId(UUID orderId) {
        try{
            return Optional.of(
                    entityManager.createNamedQuery("Payment.findByOrderId", Payment.class)
                            .setParameter("orderId",orderId)
                            .getSingleResult()
            );
        }catch (NoResultException e){
            return Optional.empty();
        }
    }

    public BigDecimal calculateTotalPaidByCustomerId(UUID customerId) {
        return entityManager.createQuery("""
            SELECT COALESCE(SUM(p.totalAmount), 0)
            FROM Payment p
            WHERE p.order.customer.id = :customerId
            AND p.paymentStatus = :status
            """, BigDecimal.class)
                .setParameter("customerId", customerId)
                .setParameter("status", PaymentStatus.PAID)
                .getSingleResult();
    }

    public List<MonthlySpendingResponse> calculateMonthlySpending(UUID customerId) {

        List<Object[]> rows = entityManager.createNativeQuery("""
            SELECT
                DATE_FORMAT(p.paid_at, '%Y-%m') AS month,
                COALESCE(SUM(p.total_amount), 0) AS amount
            FROM payments p
            INNER JOIN orders o
                ON o.id = p.order_id
            WHERE o.customer_id = ?
              AND p.status = 'PAID'
            GROUP BY DATE_FORMAT(p.paid_at, '%Y-%m')
            ORDER BY month
            """)
                .setParameter(1, customerId)
                .getResultList();

        return rows.stream()
                .map(row -> new MonthlySpendingResponse(
                        (String) row[0],
                        (BigDecimal) row[1]
                ))
                .toList();
    }

    public BigDecimal calculateTotalRevenue() {
        return entityManager.createQuery("SELECT COALESCE(SUM(p.totalAmount), 0) FROM Payment p " +
                                "WHERE p.paymentStatus = :status",
                        BigDecimal.class)
                .setParameter("status", PaymentStatus.PAID)
                .getSingleResult();
    }

    public List<MonthlyRevenueResponse> calculateMonthlyRevenue() {

        List<Object[]> rows =
                entityManager.createNativeQuery(
                                """
                                SELECT
                                    DATE_FORMAT(paid_at, '%Y-%m') AS month,
                                    COALESCE(SUM(total_amount), 0) AS revenue
                                FROM payments
                                WHERE status = 'PAID'
                                GROUP BY DATE_FORMAT(paid_at, '%Y-%m')
                                ORDER BY month
                                """
                        )
                        .getResultList();

        return rows.stream()
                .map(row -> new MonthlyRevenueResponse(
                        (String) row[0],
                        (BigDecimal) row[1]
                ))
                .toList();
    }


    public Optional<ShipmentTracking> findLatestByShipmentId(UUID shipmentId) {
        List<ShipmentTracking> result =
                entityManager.createQuery("SELECT st FROM ShipmentTracking st " +
                                "WHERE st.shipment.id = :shipmentId ORDER BY st.trackingTimestamp DESC", ShipmentTracking.class)
                        .setParameter("shipmentId", shipmentId)
                        .setMaxResults(1)
                        .getResultList();

        return result.stream().findFirst();
    }


}
