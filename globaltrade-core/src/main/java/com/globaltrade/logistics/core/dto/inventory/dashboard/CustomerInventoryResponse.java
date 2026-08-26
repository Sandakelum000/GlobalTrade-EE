package com.globaltrade.logistics.core.dto.inventory.dashboard;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerInventoryResponse(
        UUID inventoryId,
        String productName,
        String warehouseName,
        Integer availableQuantity,
        BigDecimal sellingPrice,
        LocalDateTime createdAt
) {
}
