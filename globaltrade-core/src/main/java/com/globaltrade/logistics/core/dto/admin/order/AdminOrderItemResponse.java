package com.globaltrade.logistics.core.dto.admin.order;

import java.math.BigDecimal;
import java.util.UUID;

public record AdminOrderItemResponse(
        UUID orderItemId,
        UUID productId,
        String productName,
        UUID inventoryId,
        String inventoryNumber,
        UUID warehouseId,
        String warehouseName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}
