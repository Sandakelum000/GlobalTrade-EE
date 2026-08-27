package com.globaltrade.logistics.core.dto.admin.inventory;

import com.globaltrade.logistics.core.entity.warehouse.InventoryStatus;

import java.util.UUID;

public record AdminInventoryListResponse(
        UUID inventoryId,
        String inventoryNumber,
        UUID productId,
        String productName,
        UUID warehouseId,
        String warehouseName,
        int quantity,
        int reservedQuantity,
        int availableQuantity,
        int reorderLevel,
        boolean lowStock,
        boolean outOfStock,
        InventoryStatus status
) {
}
