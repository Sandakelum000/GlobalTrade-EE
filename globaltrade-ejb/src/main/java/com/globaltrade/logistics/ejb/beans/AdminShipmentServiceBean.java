package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.dto.admin.order.AdminTrackingResponse;
import com.globaltrade.logistics.core.dto.admin.order.PageResponse;
import com.globaltrade.logistics.core.dto.admin.shipment.AdminShipmentDetailsResponse;
import com.globaltrade.logistics.core.dto.admin.shipment.AdminShipmentItemResponse;
import com.globaltrade.logistics.core.dto.admin.shipment.AdminShipmentListResponse;
import com.globaltrade.logistics.core.entity.order.OrderItem;
import com.globaltrade.logistics.core.entity.order.shipment.Shipment;
import com.globaltrade.logistics.core.entity.order.shipment.ShipmentItem;
import com.globaltrade.logistics.core.entity.order.shipment.ShipmentStatus;
import com.globaltrade.logistics.core.entity.order.shipment.tracking.ShipmentTracking;
import com.globaltrade.logistics.core.entity.product.Product;
import com.globaltrade.logistics.core.entity.warehouse.Inventory;
import com.globaltrade.logistics.core.exception.ResourceNotFoundException;
import com.globaltrade.logistics.core.service.AdminShipmentService;
import com.globaltrade.logistics.ejb.repository.AdminShipmentRepository;
import com.globaltrade.logistics.ejb.repository.ShipmentRepository;
import com.globaltrade.logistics.ejb.repository.ShipmentTrackingRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@Stateless
public class AdminShipmentServiceBean implements AdminShipmentService {

    @Inject
    private AdminShipmentRepository adminShipmentRepository;
    @Inject
    private ShipmentRepository shipmentRepository;
    @Inject
    private ShipmentTrackingRepository shipmentTrackingRepository;

    @Override
    @RolesAllowed("ADMIN")
    @Transactional(Transactional.TxType.SUPPORTS)
    public PageResponse<AdminShipmentListResponse> getShipments(String search, ShipmentStatus status, UUID warehouseId, String sortBy, String direction, int page, int size) {
        if(page < 0){
            throw new IllegalArgumentException("page cannot be negative");
        }

        if(size < 1 || size > 100){
            throw new IllegalArgumentException("size must be between 1 and 100");
        }

        List<Shipment> shipments =
                adminShipmentRepository.findShipments(search, status, warehouseId, sortBy, direction, page, size);

        long totalElements = adminShipmentRepository.countShipments(search, status, warehouseId);


        List<AdminShipmentListResponse> content = shipments.stream()
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
    @Transactional(Transactional.TxType.SUPPORTS)
    public AdminShipmentDetailsResponse getShipmentDetails(UUID shipmentId) {
        if (shipmentId == null) {
            throw new IllegalArgumentException("Shipment ID cannot be null");
        }

        Shipment shipment =
                shipmentRepository.findById(shipmentId)
                        .orElseThrow(() -> new ResourceNotFoundException("Shipment " + shipmentId + " not found"));

        List<AdminShipmentItemResponse> items = shipment.getItems()
                        .stream()
                        .map(this::toShipmentItemResponse)
                        .toList();

        List<AdminTrackingResponse> tracking = shipmentTrackingRepository
                        .findByShipmentIdOrderByTrackingTimestampAsc(shipmentId)
                        .stream()
                        .map(this::toTrackingResponse)
                        .toList();

        return new AdminShipmentDetailsResponse(
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
                shipment.getRouteName(),

                items,
                tracking
        );
    }


    private AdminShipmentListResponse toListResponse(Shipment shipment) {

        return new AdminShipmentListResponse(
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

    private AdminShipmentItemResponse toShipmentItemResponse(ShipmentItem shipmentItem) {

        OrderItem orderItem = shipmentItem.getOrderItem();

        if (orderItem == null) {
            throw new ResourceNotFoundException("OrderItem is missing for ShipmentItem " + shipmentItem.getId());
        }

        Inventory inventory = orderItem.getInventory();

        if (inventory == null) {
            throw new ResourceNotFoundException("Inventory is missing for OrderItem " + orderItem.getId());
        }
        Product product = inventory.getProduct();

        if (product == null) {
            throw new ResourceNotFoundException("Product is missing for Inventory " + inventory.getId());
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

    private AdminTrackingResponse toTrackingResponse(ShipmentTracking tracking) {
        return new AdminTrackingResponse(
                tracking.getId(),
                tracking.getStatus(),
                tracking.getDescription(),
                tracking.getLocation(),
                tracking.getTrackingTimestamp()
        );
    }
}
