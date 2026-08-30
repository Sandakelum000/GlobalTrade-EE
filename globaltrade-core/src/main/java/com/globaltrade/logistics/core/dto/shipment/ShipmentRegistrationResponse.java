package com.globaltrade.logistics.core.dto.shipment;

import com.globaltrade.logistics.core.entity.common.Address;
import com.globaltrade.logistics.core.entity.order.shipment.ShipmentStatus;
import com.globaltrade.logistics.core.service.AuditableResponse;

import java.math.BigDecimal;
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
        Address originAddress,
        Address destinationAddress,
        LocalDateTime shippedAt,
        LocalDateTime estimatedDeliveryDate,
        LocalDateTime deliveredAt,

        BigDecimal routeDistanceKm,
        BigDecimal routeEstimatedHours,
        Integer routeRiskScore,
        String routeName
) implements AuditableResponse {
    @Override
    public UUID getEntityId() {
        return id;
    }
}
