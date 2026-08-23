package com.globaltrade.logistics.core.dto.shipment;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.UUID;

public record ShipmentRegistrationRequest(
        @NotNull(message = "Order id required")
        UUID orderId
) implements Serializable {
}
