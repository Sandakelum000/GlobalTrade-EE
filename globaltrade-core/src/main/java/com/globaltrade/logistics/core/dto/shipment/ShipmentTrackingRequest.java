package com.globaltrade.logistics.core.dto.shipment;

import com.globaltrade.logistics.core.entity.order.shipment.tracking.TrackingStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ShipmentTrackingRequest(
        @NotNull(message = "Tracking status is required")
        TrackingStatus status,
        @NotBlank(message = "Tracking description is required.")
        String description,
        @NotBlank(message = "Tracking location is required")
        String location
) {
}
