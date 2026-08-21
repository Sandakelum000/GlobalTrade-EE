package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.annotation.Audited;
import com.globaltrade.logistics.core.dto.shipment.*;
import com.globaltrade.logistics.core.entity.audit.AuditAction;
import com.globaltrade.logistics.core.entity.order.Order;
import com.globaltrade.logistics.core.entity.order.OrderItem;
import com.globaltrade.logistics.core.entity.order.OrderStatus;
import com.globaltrade.logistics.core.entity.order.shipment.Shipment;
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
import java.util.List;
import java.util.UUID;

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
    private ShipmentTrackingRepository  shipmentTrackingRepository;

    @Override
    @RolesAllowed({"ADMIN"})
    @Transactional(Transactional.TxType.REQUIRED)
    public ShipmentRegistrationResponse createShipment(ShipmentRegistrationRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new RuntimeException("Order ID " + request.orderId() + " not found"));

        Warehouse warehouse = warehouseRepository.getWarehouseById(request.warehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse ID " + request.warehouseId() + " not found"));

        if(order.getOrderStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("This order is not confirmed yet");
        }

        if(order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalStateException("Order Items are empty");
        }

        String nextShipmentNumber = numberSequenceService.next(SEQUENCE_KEY, PREFIX, WIDTH);

        Shipment shipment = Shipment.builder()
                .shipmentNumber(nextShipmentNumber)
                .order(order)
                .warehouse(warehouse)
                .status(ShipmentStatus.PENDING)
                .shippedAt(null)
                .estimatedDeliveryDate(LocalDateTime.now().plusDays(7))
                .deliveredAt(null)
                .build();

        shipmentRepository.save(shipment);
        return toResponse(shipment);
    }

    private ShipmentRegistrationResponse toResponse(@NotNull Shipment shipment) {
        return new  ShipmentRegistrationResponse(
                shipment.getId(),
                shipment.getShipmentNumber(),
                shipment.getOrder().getId(),
                shipment.getOrder().getOrderNumber(),
                shipment.getWarehouse().getId(),
                shipment.getWarehouse().getName(),
                shipment.getStatus(),
                shipment.getShippedAt(),
                shipment.getEstimatedDeliveryDate(),
                shipment.getDeliveredAt()
        );
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

        if(shipment.getStatus() != ShipmentStatus.PENDING) {
            throw new IllegalStateException("This shipment is not confirmed yet");
        }

        Order order = shipment.getOrder();

        // update inventory
        for (OrderItem orderItem : order.getItems()) {

            Inventory inventory = orderItem.getInventory();
            if(inventory == null){
                throw new IllegalStateException("Inventory is not found for ordered item: " + orderItem.getId());
            }
            inventory.removeStock(orderItem.getQuantity());

            if(inventory.getReservedQuantity() >= orderItem.getQuantity()){
                inventory.releaseReservedStock(orderItem.getQuantity());
            }
        }
        shipment.setStatus(ShipmentStatus.SHIPPED);
        ShipmentTracking shipmentTracking = ShipmentTracking.builder()
                .shipment(shipment)
                .status(TrackingStatus.PICKED_UP)
                .description("Shipment picked up from warehouse id: " + shipment.getWarehouse().getId())
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
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found id: " + shipmentId));

        if (shipment.getStatus() == ShipmentStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update tracking for a cancelled shipment");
        }

        if (shipment.getStatus() == ShipmentStatus.DELIVERED) {
            throw new IllegalStateException("Cannot update tracking after delivery");
        }

        ShipmentTracking tracking = ShipmentTracking.builder()
                .shipment(shipment)
                .status(request.status())
                .description(request.description())
                .location(request.location())
                .trackingTimestamp(LocalDateTime.now())
                .build();

        shipmentTrackingRepository.save(tracking);
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
    @RolesAllowed({"ADMIN"})
    @Audited(
            action = AuditAction.DELIVER,
            entity = "Shipment"
    )
    @Transactional(Transactional.TxType.REQUIRED)
    public ShipmentRegistrationResponse deliverShipment(UUID shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found: " + shipmentId));

        if (shipment.getStatus() == ShipmentStatus.DELIVERED) {
            throw new IllegalStateException("Shipment is already delivered");
        }

        if (shipment.getStatus() != ShipmentStatus.OUT_FOR_DELIVERY) {
            throw new IllegalStateException("Only OUT_FOR_DELIVERY shipments can be delivered");
        }

        shipment.setStatus(ShipmentStatus.DELIVERED);
        shipment.setDeliveredAt(LocalDateTime.now());
        shipment.getOrder().setOrderStatus(OrderStatus.DELIVERED);

        ShipmentTracking tracking = ShipmentTracking.builder()
                .shipment(shipment)
                .status(TrackingStatus.DELIVERED)
                .description("Shipment id: "+shipment.getId()+" delivered successfully")
                .location(shipment.getWarehouse().getName())
                .trackingTimestamp(LocalDateTime.now())
                .build();

        shipmentTrackingRepository.save(tracking);
        return toResponse(shipment);
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
                        shipment.getDeliveredAt()
                )).toList();
    }
}
