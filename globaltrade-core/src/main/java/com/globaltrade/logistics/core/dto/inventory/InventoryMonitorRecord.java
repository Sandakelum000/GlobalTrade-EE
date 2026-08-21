package com.globaltrade.logistics.core.dto.inventory;


import java.io.Serializable;
import java.util.UUID;


public record InventoryMonitorRecord(
        UUID inventoryId,
        String inventoryNumber,
        UUID productId,
        String productNumber,
        String warehouseName,
        Integer availableQuantity,
        Integer recorderLevel
){

}
