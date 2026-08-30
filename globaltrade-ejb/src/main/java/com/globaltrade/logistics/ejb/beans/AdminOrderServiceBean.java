package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.dto.admin.order.*;
import com.globaltrade.logistics.core.entity.audit.AuditAction;
import com.globaltrade.logistics.core.entity.order.Order;
import com.globaltrade.logistics.core.entity.order.OrderItem;
import com.globaltrade.logistics.core.entity.order.OrderStatus;
import com.globaltrade.logistics.core.entity.order.shipment.Shipment;
import com.globaltrade.logistics.core.entity.order.shipment.ShipmentItem;
import com.globaltrade.logistics.core.entity.product.Product;
import com.globaltrade.logistics.core.entity.warehouse.Inventory;
import com.globaltrade.logistics.core.exception.AdminOrderServiceException;
import com.globaltrade.logistics.core.exception.ResourceNotFoundException;
import com.globaltrade.logistics.core.service.AdminOrderService;
import com.globaltrade.logistics.core.service.AuditLogService;
import com.globaltrade.logistics.ejb.repository.AdminOrderRepository;
import com.globaltrade.logistics.ejb.repository.OrderRepository;
import com.globaltrade.logistics.ejb.repository.ShipmentRepository;
import com.globaltrade.logistics.ejb.repository.ShipmentTrackingRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Stateless
public class AdminOrderServiceBean implements AdminOrderService {

    @Inject
    private AdminOrderRepository adminOrderRepository;
    @Inject
    private OrderRepository orderRepository;
    @Inject
    private ShipmentRepository shipmentRepository;
    @Inject
    private ShipmentTrackingRepository shipmentTrackingRepository;
    @Inject
    private AuditLogService auditLogService;

    @Override
    @RolesAllowed("ADMIN")
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public PageResponse<AdminOrderListResponse> getOrders(String search, OrderStatus status, String sortBy, String direction, int page, int size) {
        if (page < 0) {
            throw new AdminOrderServiceException("Page number should be greater than zero");
        }

        if (size < 1 || size > 100) {
            throw new AdminOrderServiceException("Size should be between 1 and 100");
        }

        List<Order> orders = adminOrderRepository.findOrders(search, status, sortBy, direction, page, size);

        long totalElements = adminOrderRepository.countOrders(search, status);

        List<AdminOrderListResponse> content = orders.stream()
                .map(this::toListResponse)
                .toList();

        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new PageResponse<>(
                content,
                page,
                size,
                totalElements,
                totalPages
        );

    }

    @Override
    @RolesAllowed("ADMIN")
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public AdminOrderDetailsResponse getOrderDetails(UUID orderId) {
        if (orderId == null) {
            throw new AdminOrderServiceException("Order ID cannot be null");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AdminOrderServiceException("Order " + orderId + " not found"));

        AdminCustomerResponse customer =
                new AdminCustomerResponse(
                        order.getCustomer().getId(),
                        order.getCustomer().getFirstName(),
                        order.getCustomer().getLastName(),
                        order.getCustomer().getEmail(),
                        order.getCustomer().getMobile1(),
                        order.getCustomer().getMobile2(),
                        order.getCustomer().getUser().getUsername()
                );

        List<AdminOrderItemResponse> items =
                order.getItems()
                        .stream()
                        .map(this::toOrderItemResponse)
                        .toList();

        List<AdminShipmentResponse> shipments =
                shipmentRepository.findByOrderId(orderId)
                        .stream()
                        .map(this::toShipmentResponse)
                        .toList();

        return new AdminOrderDetailsResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getOrderStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                customer,
                order.getShippingAddress(),
                items,
                shipments
        );
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void cancelExpiredUnpaidOrders() {
        LocalDateTime now = LocalDateTime.now().minusHours(24);

        List<Order> expiredUnpaidOrders = orderRepository.findExpiredUnpaidOrders(now);
        for (Order order : expiredUnpaidOrders) {
            order.setOrderStatus(OrderStatus.CANCELLED);
            auditLogService.log(null, AuditAction.CANCEL, "Order", order.getId().toString(),
                    "Order " + order.getOrderNumber() + " has been cancelled due to unpaid order within 24 hours");
        }
    }

    private AdminOrderListResponse toListResponse(Order order) {

        int itemCount = order.getItems() == null
                ? 0
                : order.getItems().size();

        int shipmentCount = order.getShipments() == null
                ? 0
                : order.getShipments().size();

        return new AdminOrderListResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomer().getId(),
                order.getCustomer().getFirstName()
                        + " "
                        + order.getCustomer().getLastName(),
                order.getCustomer().getEmail(),
                order.getOrderStatus(),
                order.getTotalAmount(),
                itemCount,
                shipmentCount,
                order.getCreatedAt()
        );
    }

    private AdminOrderItemResponse toOrderItemResponse(OrderItem orderItem) {

        if (orderItem.getInventory() == null) {
            throw new ResourceNotFoundException("Inventory is missing for OrderItem " + orderItem.getId());
        }

        Inventory inventory = orderItem.getInventory();

        if (inventory.getProduct() == null) {
            throw new ResourceNotFoundException("Product is missing for Inventory " + inventory.getId());
        }

        if (inventory.getWarehouse() == null) {
            throw new ResourceNotFoundException("Warehouse is missing for Inventory " + inventory.getId());
        }

        return new AdminOrderItemResponse(
                orderItem.getId(),
                inventory.getProduct().getId(),
                inventory.getProduct().getTitle(),
                inventory.getId(),
                inventory.getInventoryNumber(),
                inventory.getWarehouse().getId(),
                inventory.getWarehouse().getName(),
                orderItem.getQuantity(),
                orderItem.getUnitPrice(),
                orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()))
        );
    }

    private AdminShipmentResponse toShipmentResponse(Shipment shipment) {

        List<AdminShipmentItemResponse> items =
                shipment.getItems()
                        .stream()
                        .map(this::toShipmentItemResponse)
                        .toList();

        List<AdminTrackingResponse> tracking =
                shipmentTrackingRepository.findByShipmentIdOrderByTrackingTimestampAsc(shipment.getId())
                        .stream()
                        .map(trackingRecord ->
                                new AdminTrackingResponse(
                                        trackingRecord.getId(),
                                        trackingRecord.getStatus(),
                                        trackingRecord.getDescription(),
                                        trackingRecord.getLocation(),
                                        trackingRecord.getTrackingTimestamp()
                                )
                        )
                        .toList();

        return new AdminShipmentResponse(
                shipment.getId(),
                shipment.getShipmentNumber(),
                shipment.getWarehouse().getId(),
                shipment.getWarehouse().getName(),
                shipment.getStatus(),
                shipment.getOriginAddress(),
                shipment.getDestinationAddress(),
                shipment.getShippedAt(),
                shipment.getEstimatedDeliveryDate(),
                shipment.getDeliveredAt(),
                items,
                tracking
        );
    }

    private AdminShipmentItemResponse toShipmentItemResponse(ShipmentItem shipmentItem) {

        OrderItem orderItem = shipmentItem.getOrderItem();

        if (orderItem == null) {
            throw new ResourceNotFoundException("OrderItem is missing for ShipmentItem " + shipmentItem.getId());
        }

        Inventory inventory = orderItem.getInventory();

        if (inventory == null) {
            throw new IllegalStateException("Inventory is missing for OrderItem " + orderItem.getId());
        }

        Product product = inventory.getProduct();

        if (product == null) {
            throw new IllegalStateException("Product is missing for Inventory " + inventory.getId());
        }

        return new AdminShipmentItemResponse(
                shipmentItem.getId(),
                orderItem.getId(),
                product.getId(),
                product.getTitle(),
                orderItem.getQuantity(),
                shipmentItem.getStatus()
        );
    }
}
