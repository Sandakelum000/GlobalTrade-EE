package com.globaltrade.logistics.core.dto.customer.dashboard;

import com.globaltrade.logistics.core.entity.order.shipment.ShipmentStatus;
import com.globaltrade.logistics.core.entity.order.shipment.tracking.TrackingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerShipmentResponse (
        UUID shipmentId,
        String shipmentNumber,
        String orderNumber,
        String warehouseName,
        ShipmentStatus status,
        LocalDateTime estimatedDeliveryDate,
        BigDecimal routeDistanceKm,
        BigDecimal routeEstimatedHours,
        Integer routeRiskScore,
        String routeName,
        TrackingStatus latestTrackingStatus,
        String latestLocation,
        LocalDateTime latestTrackingTime
){
}
