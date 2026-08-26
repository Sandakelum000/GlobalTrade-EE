package com.globaltrade.logistics.core.dto.admin.order;

import com.globaltrade.logistics.core.entity.order.shipment.tracking.TrackingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminTrackingResponse(
        UUID trackingId,
        TrackingStatus status,
        String description,
        String location,
        LocalDateTime trackingTimestamp
) {
}
