package com.globaltrade.logistics.core.dto.order;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record OrderRegistrationRequest(
        @NotNull(message = "At least one order item is required")
        List<OrderItemRequest> items
) {
}
