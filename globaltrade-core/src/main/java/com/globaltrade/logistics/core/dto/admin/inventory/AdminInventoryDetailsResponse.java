package com.globaltrade.logistics.core.dto.admin.inventory;

import com.globaltrade.logistics.core.entity.company.Company;
import com.globaltrade.logistics.core.entity.warehouse.InventoryStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AdminInventoryDetailsResponse (
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

        InventoryStatus status,
        BigDecimal sellingPrice,
        BigDecimal buyingPrice,

        String vendorName,
        String country,

        LocalDateTime createdAt
){
}
