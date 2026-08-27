package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.admin.inventory.AdminInventoryDetailsResponse;
import com.globaltrade.logistics.core.dto.admin.inventory.AdminInventoryListResponse;
import com.globaltrade.logistics.core.dto.admin.inventory.ProductOptionResponse;
import com.globaltrade.logistics.core.dto.admin.inventory.WarehouseOptionResponse;
import com.globaltrade.logistics.core.dto.admin.order.PageResponse;
import jakarta.ejb.Local;

import java.util.List;
import java.util.UUID;

@Local
public interface AdminInventoryService {

    PageResponse<AdminInventoryListResponse> getInventories(
            String search,
            UUID warehouseId,
            UUID productId,
            Boolean lowStock,
            Boolean outOfStock,
            String sortBy,
            String direction,
            int page,
            int size
    );
    AdminInventoryDetailsResponse getInventoryDetails(UUID inventoryId);
    List<WarehouseOptionResponse> getWarehouseOptions();
    List<ProductOptionResponse> getProductOptions(String search);
}
