package com.globaltrade.logistics.core.dto.shipment;

import com.globaltrade.logistics.core.entity.order.shipment.ShipmentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ShipmentRegistrationResponse(
        UUID id,
        String shipmentNumber,
        UUID orderId,
        String orderNumber,
        UUID warehouseId,
        String warehouseName,
        ShipmentStatus status,
        LocalDateTime shippedAt,
        LocalDateTime estimatedDeliveryDate,
        LocalDateTime deliveredAt
) {
}
