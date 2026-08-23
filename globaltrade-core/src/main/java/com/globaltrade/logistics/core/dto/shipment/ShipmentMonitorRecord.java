package com.globaltrade.logistics.core.dto.shipment;

import com.globaltrade.logistics.core.entity.order.shipment.ShipmentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record ShipmentMonitorRecord(
        UUID id,
        String shipmentNumber,
        UUID orderId,
        String orderNumber,
        ShipmentStatus status,
        String warehouseName,
        LocalDateTime shippedAt,
        LocalDateTime estimatedDeliveryDate,
        LocalDateTime deliveredAt,

        BigDecimal routeDistanceKm,
        BigDecimal routeEstimatedHours,
        Integer routeRiskScore,
        String routeName
) {
}
