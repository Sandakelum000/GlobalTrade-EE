package com.globaltrade.logistics.core.dto.admin.dashboard;

import com.globaltrade.logistics.core.entity.order.shipment.ShipmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminShipmentResponse (
        UUID shipmentId,
        String shipmentNumber,
        UUID orderId,
        String orderNumber,
        UUID warehouseId,
        String warehouseName,
        ShipmentStatus status,
        LocalDateTime shippedAt,
        LocalDateTime estimatedDeliveryDate,
        LocalDateTime deliveredAt
){
}
