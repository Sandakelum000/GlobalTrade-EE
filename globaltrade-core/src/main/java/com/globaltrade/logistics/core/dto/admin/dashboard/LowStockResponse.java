package com.globaltrade.logistics.core.dto.admin.dashboard;

import java.math.BigDecimal;
import java.util.UUID;

public record LowStockResponse(
        UUID id,
        String inventoryNumber,
        UUID productId,
        String productTitle,
        UUID warehouseId,
        String warehouseName,
        Integer availableQuantity,
        Integer reorderLevel
) {
}
