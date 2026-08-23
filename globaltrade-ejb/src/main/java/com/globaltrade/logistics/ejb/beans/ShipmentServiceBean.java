package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.annotation.Audited;
import com.globaltrade.logistics.core.dto.shipment.*;
import com.globaltrade.logistics.core.entity.audit.AuditAction;
import com.globaltrade.logistics.core.entity.order.Order;
import com.globaltrade.logistics.core.entity.order.OrderItem;
import com.globaltrade.logistics.core.entity.order.OrderStatus;
import com.globaltrade.logistics.core.entity.order.shipment.Shipment;
import com.globaltrade.logistics.core.entity.order.shipment.ShipmentItem;
import com.globaltrade.logistics.core.entity.order.shipment.ShipmentItemStatus;
import com.globaltrade.logistics.core.entity.order.shipment.ShipmentStatus;
import com.globaltrade.logistics.core.entity.order.shipment.tracking.ShipmentTracking;
import com.globaltrade.logistics.core.entity.order.shipment.tracking.TrackingStatus;
import com.globaltrade.logistics.core.entity.warehouse.Inventory;
import com.globaltrade.logistics.core.entity.warehouse.Warehouse;
import com.globaltrade.logistics.core.service.NumberSequenceService;
import com.globaltrade.logistics.core.service.ShipmentService;
import com.globaltrade.logistics.ejb.repository.*;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class ShipmentServiceBean implements ShipmentService {
    private static final String SEQUENCE_KEY = "SHIPMENT";
    private static final String PREFIX = "SHI";
    private static final int WIDTH = 3;

    @Inject
    private OrderRepository orderRepository;
    @Inject
    private WarehouseRepository warehouseRepository;
    @Inject
    private NumberSequenceService numberSequenceService;
    @Inject
    private ShipmentRepository shipmentRepository;
    @Inject
    private ShipmentItemRepository shipmentItemRepository;
    @Inject
    private ShipmentTrackingRepository shipmentTrackingRepository;

    @Override
    @Audited(
            action = AuditAction.CREATE,
            entity = "Shipment"
    )
    @Transactional(Transactional.TxType.REQUIRED)
    public List<ShipmentRegistrationResponse> createShipmentsForOrder(UUID orderId) {

        if (orderId == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        if (shipmentRepository.existsByOrderId(orderId)) {
            return shipmentRepository.findByOrderId(orderId)
                    .stream()
                    .map(this::toResponse)
                    .toList();
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order ID " + orderId + " not found"));

        if (order.getOrderStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Order " + order.getOrderNumber() + " is not confirmed yet");
        }

        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalStateException("Order " + order.getOrderNumber() + " has no order items");
        }

        if (order.getShippingAddress() == null) {
            throw new IllegalStateException("Order " + order.getOrderNumber() + " has no shipping address");
        }


        Map<UUID, List<OrderItem>> itemsByWarehouse = toItemsByWarehouse(order);
        List<ShipmentRegistrationResponse> responses = new ArrayList<>();

        for (Map.Entry<UUID, List<OrderItem>> entry : itemsByWarehouse.entrySet()) {

            UUID warehouseId = entry.getKey();
            List<OrderItem> warehouseItems = entry.getValue();

            Warehouse warehouse = warehouseRepository.getWarehouseById(warehouseId)
                            .orElseThrow(() -> new IllegalArgumentException("Warehouse ID " + warehouseId + " not found"));

            if (warehouse.getAddress() == null) {
                throw new IllegalStateException("Warehouse " + warehouse.getName() + " does not have an address");
            }

            String shipmentNumber = numberSequenceService.next(SEQUENCE_KEY, PREFIX, WIDTH);

            Shipment shipment = Shipment.builder()
                    .shipmentNumber(shipmentNumber)
                    .order(order)
                    .warehouse(warehouse)
                    .originAddress(warehouse.getAddress())
                    .destinationAddress(order.getShippingAddress())
                    .status(ShipmentStatus.PENDING)
                    .shippedAt(null)
                    .estimatedDeliveryDate(LocalDateTime.now().plusDays(7))
                    .deliveredAt(null)
                    .build();

            shipmentRepository.save(shipment);

            for (OrderItem orderItem : warehouseItems) {
                ShipmentItem shipmentItem = ShipmentItem.builder()
                                .shipment(shipment)
                                .orderItem(orderItem)
                                .status(ShipmentItemStatus.PENDING)
                                .build();

                shipmentItemRepository.save(shipmentItem);
            }
            responses.add(toResponse(shipment));
        }

        return responses;
    }

    @Override
    @RolesAllowed({"ADMIN"})
    @Audited(
            action = AuditAction.SHIP,
            entity = "Shipment"
    )
    @Transactional(Transactional.TxType.REQUIRED)
    public ShipmentRegistrationResponse shipShipment(UUID shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found id: " + shipmentId));

        if (shipment.getStatus() != ShipmentStatus.PENDING) {
            throw new IllegalStateException("This shipment is not confirmed yet");
        }

        //shipmeny Items belong to shipment
        List<ShipmentItem> shipmentItems = shipmentItemRepository.findByShipmentId(shipmentId);

        if (shipmentItems.isEmpty()) {
            throw new IllegalStateException("Shipment items not found");
        }

        for (ShipmentItem shipmentItem : shipmentItems) {

            OrderItem orderItem = shipmentItem.getOrderItem();
            if (orderItem == null) {
                throw new IllegalStateException("Order Item is missing for shipment item id: " + shipmentItem.getId());
            }

            Inventory inventory = orderItem.getInventory();

            if (inventory == null) {
                throw new IllegalStateException("Inventory is missing for shipment item id: " + shipmentItem.getId());
            }
            if (!inventory.getWarehouse().getId().equals(shipment.getWarehouse().getId())) {
                throw new IllegalStateException("Inventory warehouse does not match shipment warehouse");
            }

            inventory.removeStock(orderItem.getQuantity()); // actual stock update

            if (inventory.getReservedQuantity() >= orderItem.getQuantity()) { // reserve stock update
                inventory.releaseReservedStock(orderItem.getQuantity());
            }

            shipmentItem.setStatus(ShipmentItemStatus.SHIPPED);
        }

        shipment.setStatus(ShipmentStatus.SHIPPED);
        shipment.setShippedAt(LocalDateTime.now());


        ShipmentTracking shipmentTracking = ShipmentTracking.builder()
                .shipment(shipment)
                .status(TrackingStatus.PICKED_UP)
                .description("Shipment picked up from warehouse: " + shipment.getWarehouse().getName())
                .location("Warehouse: " + shipment.getWarehouse().getName())
                .trackingTimestamp(LocalDateTime.now())
                .build();

        shipmentTrackingRepository.save(shipmentTracking);
        return toResponse(shipment);
    }

    @Override
    @Audited(
            action = AuditAction.UPDATE,
            entity = "ShipmentTracking"
    )
    @Transactional(Transactional.TxType.REQUIRED)
    public ShipmentTrackingResponse updateTracking(UUID shipmentId, ShipmentTrackingRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Tracking request cannot be null");
        }
        if (request.status() == null) {
            throw new IllegalArgumentException("Tracking status is required");
        }
        if (request.location() == null || request.location().isBlank()) {
            throw new IllegalArgumentException("Tracking location is required");
        }

        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found id: " + shipmentId));

        if (shipment.getStatus() == ShipmentStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update tracking for a cancelled shipment");
        }

        if (shipment.getStatus() == ShipmentStatus.DELIVERED) {
            throw new IllegalStateException("Cannot update tracking after delivery");
        }

        ShipmentStatus newShipmentStatus = convertTrackingStatusToShipmentStatus(request.status());
        validateShipmentTransition(shipment.getStatus(), newShipmentStatus); // avoid backwards status

        LocalDateTime now = LocalDateTime.now();

        ShipmentTracking tracking = ShipmentTracking.builder()
                .shipment(shipment)
                .status(request.status())
                .description(request.description())
                .location(request.location())
                .trackingTimestamp(now)
                .build();

        shipmentTrackingRepository.save(tracking);
        shipment.setStatus(newShipmentStatus); // update shipment status


        if (request.status() == TrackingStatus.PICKED_UP || shipment.getShippedAt() == null) {
            shipment.setShippedAt(now);
        }

        if (request.status() == TrackingStatus.DELIVERED) {
            shipment.setDeliveredAt(now); // shipment delivered

            //shipment Items Delivered
            List<ShipmentItem> shipmentItems = shipmentItemRepository.findByShipmentId(shipmentId);

            for (ShipmentItem shipmentItem : shipmentItems) {
                shipmentItem.setStatus(ShipmentItemStatus.DELIVERED);
            }
            updateOrderStatusIfAllShipmentsDelivered(shipment);
        }
        return new ShipmentTrackingResponse(
                tracking.getId(),
                shipment.getId(),
                tracking.getStatus(),
                tracking.getDescription(),
                tracking.getLocation(),
                tracking.getTrackingTimestamp()
        );
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<ShipmentMonitorRecord> findActiveShipments() {
        return shipmentRepository.findActiveShipments()
                .stream()
                .map(shipment -> new ShipmentMonitorRecord(
                        shipment.getId(),
                        shipment.getShipmentNumber(),
                        shipment.getOrder().getId(),
                        shipment.getOrder().getOrderNumber(),
                        shipment.getStatus(),
                        shipment.getWarehouse().getName(),
                        shipment.getShippedAt(),
                        shipment.getEstimatedDeliveryDate(),
                        shipment.getDeliveredAt(),
                        shipment.getRouteDistanceKm(),
                        shipment.getRouteEstimatedHours(),
                        shipment.getRouteRiskScore(),
                        shipment.getRouteName()
                )).toList();
    }


    private Map<UUID, List<OrderItem>> toItemsByWarehouse(Order order) {
        Map<UUID, List<OrderItem>> itemsByWarehouse = new HashMap<>();
        for (OrderItem orderItem : order.getItems()) {

            if (orderItem == null) {
                throw new IllegalStateException("Order " + order.getOrderNumber() + " contains a null order item");
            }

            Inventory inventory = orderItem.getInventory();
            if (inventory == null) {
                throw new IllegalStateException("Inventory is missing for OrderItem " + orderItem.getId());
            }

            Warehouse warehouse = inventory.getWarehouse();
            if (warehouse == null) {
                throw new IllegalStateException("Warehouse is missing for Inventory " + inventory.getInventoryNumber() + " / OrderItem " + orderItem.getId());
            }
            if (warehouse.getId() == null) {
                throw new IllegalStateException("Warehouse ID is missing for Warehouse " + warehouse.getName());
            }

            itemsByWarehouse
                    .computeIfAbsent(warehouse.getId(), key -> new ArrayList<>())
                    .add(orderItem);
        }
        return itemsByWarehouse;
    }

    private ShipmentRegistrationResponse toResponse(@NotNull Shipment shipment) {
        return new ShipmentRegistrationResponse(
                shipment.getId(),
                shipment.getShipmentNumber(),
                shipment.getOrder().getId(),
                shipment.getOrder().getOrderNumber(),
                shipment.getWarehouse().getId(),
                shipment.getWarehouse().getName(),
                shipment.getStatus(),
                shipment.getOriginAddress(),
                shipment.getDestinationAddress(),
                shipment.getShippedAt(),
                shipment.getEstimatedDeliveryDate(),
                shipment.getDeliveredAt(),
                shipment.getRouteDistanceKm(),
                shipment.getRouteEstimatedHours(),
                shipment.getRouteRiskScore(),
                shipment.getRouteName()
        );
    }


    private ShipmentStatus convertTrackingStatusToShipmentStatus(TrackingStatus trackingStatus) {
        return switch (trackingStatus) {
            case LABEL_CREATED -> ShipmentStatus.PENDING;

            case PICKED_UP -> ShipmentStatus.SHIPPED;

            case IN_TRANSIT,
                 ARRIVED_AT_PORT,
                 CUSTOMS_CLEARANCE,
                 CUSTOMS_CLEARED -> ShipmentStatus.IN_TRANSIT;

            case OUT_FOR_DELIVERY -> ShipmentStatus.OUT_FOR_DELIVERY;

            case DELIVERED -> ShipmentStatus.DELIVERED;
        };
    }

    private void validateShipmentTransition(ShipmentStatus currentStatus, ShipmentStatus newStatus) {
        boolean valid = switch (currentStatus) {

            case PENDING -> newStatus == ShipmentStatus.PENDING ||
                    newStatus == ShipmentStatus.PROCESSING;

            case PROCESSING -> newStatus == ShipmentStatus.PROCESSING ||
                    newStatus == ShipmentStatus.SHIPPED;

            case SHIPPED -> newStatus == ShipmentStatus.SHIPPED ||
                    newStatus == ShipmentStatus.IN_TRANSIT;

            case IN_TRANSIT -> newStatus == ShipmentStatus.IN_TRANSIT ||
                    newStatus == ShipmentStatus.OUT_FOR_DELIVERY ||
                    newStatus == ShipmentStatus.DELIVERED;

            case OUT_FOR_DELIVERY -> newStatus == ShipmentStatus.OUT_FOR_DELIVERY ||
                    newStatus == ShipmentStatus.DELIVERED;

            case DELIVERED, CANCELLED -> false;
        };

        if (!valid) {
            throw new IllegalStateException("Invalid shipment status transition: " + currentStatus + " -> " + newStatus);
        }
    }

    private void updateOrderStatusIfAllShipmentsDelivered(Shipment shipment) {
        Order order = shipment.getOrder();
        if (order == null) return;

        List<Shipment> shipments = shipmentRepository.findByOrderId(order.getId());
        boolean isAllDelivered = shipments.stream()
                .allMatch(s -> s.getStatus() == ShipmentStatus.DELIVERED);

        if (isAllDelivered) {
            order.setOrderStatus(OrderStatus.DELIVERED);
        }

    }
}
