package com.globaltrade.logistics.core.dto.shipment;

import com.globaltrade.logistics.core.entity.order.shipment.tracking.TrackingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ShipmentTrackingResponse(
        UUID id,
        UUID shipmentId,
        TrackingStatus status,
        String description,
        String location,
        LocalDateTime trackingTimeStamp
) {
}
