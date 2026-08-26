package com.globaltrade.logistics.core.dto.admin.shipment;

import com.globaltrade.logistics.core.dto.admin.order.AdminTrackingResponse;
import com.globaltrade.logistics.core.entity.common.Address;
import com.globaltrade.logistics.core.entity.order.shipment.ShipmentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AdminShipmentDetailsResponse(
        UUID shipmentId,
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
        String routeName,

        List<AdminShipmentItemResponse> items,
        List<AdminTrackingResponse> tracking
) {
}
