package com.globaltrade.logistics.core.dto.shipment;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ShipmentRegistrationRequest(
        @NotNull(message = "Order id required")
        UUID orderId,
        @NotNull(message = "Warehouse id is required")
        UUID warehouseId
) {
}
