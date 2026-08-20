package com.globaltrade.logistics.core.dto.order;

import java.util.UUID;

public record OrderItemRequest(
    UUID inventoryId,
    Integer quantity
) {
}
