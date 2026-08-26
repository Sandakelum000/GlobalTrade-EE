package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.dto.customer.dashboard.*;
import com.globaltrade.logistics.core.entity.order.Order;
import com.globaltrade.logistics.core.entity.order.OrderStatus;
import com.globaltrade.logistics.core.entity.order.shipment.Shipment;
import com.globaltrade.logistics.core.entity.order.shipment.ShipmentStatus;
import com.globaltrade.logistics.core.entity.order.shipment.tracking.ShipmentTracking;
import com.globaltrade.logistics.core.service.CustomerDashboardService;
import com.globaltrade.logistics.ejb.repository.*;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Stateless
public class CustomerDashboardServiceBean implements CustomerDashboardService {
    @Inject
    private CustomerRepository customerRepository;
    @Inject
    private OrderRepository orderRepository;
    @Inject
    private ShipmentRepository shipmentRepository;
    @Inject
    private ShipmentTrackingRepository  shipmentTrackingRepository;
    @Inject
    private PaymentRepository paymentRepository;

    @Override
    @RolesAllowed("CUSTOMER")
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public CustomerDashboardResponse getDashboard(UUID customerId) {
        if(customerId == null){
            throw new IllegalArgumentException("customerId is not found");
        }

        customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("customerId is not found"));

        return new CustomerDashboardResponse(
                buildSummary(customerId),
                buildRecentOrders(customerId),
                buildShipments(customerId),
                buildOrderStatus(customerId),
                buildShipmentStatus(customerId),
                buildMonthlyOrders(customerId),
                buildMonthlySpending(customerId)
        );
    }


    private DashboardSummaryResponse buildSummary(UUID customerId) {
        long totalOrders = orderRepository.countByCustomerId(customerId);

        long pendingOrders = orderRepository.countByCustomerIdAndStatus(customerId, OrderStatus.PENDING);
        long confirmedOrders = orderRepository.countByCustomerIdAndStatus(customerId, OrderStatus.CONFIRMED);

        long activeShipments = shipmentRepository.countActiveShipmentsByCustomerId(customerId);

        long deliveredShipments = shipmentRepository.countByCustomerIdAndStatus(customerId, ShipmentStatus.DELIVERED);

        BigDecimal totalSpent = paymentRepository.calculateTotalPaidByCustomerId(customerId);

        return new DashboardSummaryResponse(
                totalOrders,
                pendingOrders,
                confirmedOrders,
                activeShipments,
                deliveredShipments,
                totalSpent
        );
    }

    private List<RecentOrderResponse> buildRecentOrders(
            UUID customerId) {

        return orderRepository
                .findRecentOrdersByCustomer(customerId, 5)
                .stream()
                .map(this::toRecentOrderResponse)
                .toList();
    }

    private List<CustomerShipmentResponse> buildShipments(
            UUID customerId) {

        return shipmentRepository
                .findRecentShipmentsByCustomer(customerId, 10)
                .stream()
                .map(this::toCustomerShipmentResponse)
                .toList();
    }

    private List<OrderStatusCountResponse> buildOrderStatus(
            UUID customerId) {

        return orderRepository.countCustomerOrdersByStatus(customerId);
    }

    private List<ShipmentStatusCountResponse> buildShipmentStatus(
            UUID customerId) {

        return shipmentRepository.countShipmentsByStatus(customerId);
    }

    private List<MonthlyOrderResponse> buildMonthlyOrders(
            UUID customerId) {

        return orderRepository.countMonthlyOrders(customerId);
    }

    private List<MonthlySpendingResponse> buildMonthlySpending(
            UUID customerId) {

        return paymentRepository.calculateMonthlySpending(customerId);
    }

    private RecentOrderResponse toRecentOrderResponse(Order order) {

        return new RecentOrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCreatedAt(),
                order.getTotalAmount(),
                order.getOrderStatus()
        );
    }

    private CustomerShipmentResponse toCustomerShipmentResponse(
            Shipment shipment) {

        ShipmentTracking latest =
                shipmentTrackingRepository
                        .findLatestByShipmentId(shipment.getId())
                        .orElse(null);

        return new CustomerShipmentResponse(
                shipment.getId(),
                shipment.getShipmentNumber(),
                shipment.getOrder().getOrderNumber(),
                shipment.getWarehouse().getName(),
                shipment.getStatus(),
                shipment.getEstimatedDeliveryDate(),
                shipment.getRouteDistanceKm(),
                shipment.getRouteEstimatedHours(),
                shipment.getRouteRiskScore(),
                shipment.getRouteName(),
                latest != null ? latest.getStatus() : null,
                latest != null ? latest.getLocation() : null,
                latest != null ? latest.getTrackingTimestamp() : null
        );
    }
}
