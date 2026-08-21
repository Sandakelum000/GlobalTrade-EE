package com.globaltrade.logistics.core.dto.shipment;

import com.globaltrade.logistics.core.entity.order.shipment.tracking.TrackingStatus;
import jakarta.validation.constraints.NotNull;

public record ShipmentTrackingRequest(
        @NotNull(message = "Tracking status is required")
        TrackingStatus status,
        String description,
        String location
) {
}
