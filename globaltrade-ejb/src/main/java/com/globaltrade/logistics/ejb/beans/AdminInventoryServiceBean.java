package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.dto.admin.inventory.AdminInventoryDetailsResponse;
import com.globaltrade.logistics.core.dto.admin.inventory.AdminInventoryListResponse;
import com.globaltrade.logistics.core.dto.admin.inventory.ProductOptionResponse;
import com.globaltrade.logistics.core.dto.admin.inventory.WarehouseOptionResponse;
import com.globaltrade.logistics.core.dto.admin.order.PageResponse;
import com.globaltrade.logistics.core.entity.warehouse.Inventory;
import com.globaltrade.logistics.core.exception.ResourceNotFoundException;
import com.globaltrade.logistics.core.service.AdminInventoryService;
import com.globaltrade.logistics.ejb.repository.AdminInventoryRepository;
import com.globaltrade.logistics.ejb.repository.InventoryRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@Stateless
public class AdminInventoryServiceBean implements AdminInventoryService {

    @Inject
    private AdminInventoryRepository adminInventoryRepository;

    @Override
    @RolesAllowed("ADMIN")
    @Transactional(Transactional.TxType.SUPPORTS)
    public PageResponse<AdminInventoryListResponse> getInventories(String search, UUID warehouseId, UUID productId, Boolean lowStock, Boolean outOfStock, String sortBy, String direction, int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100"
            );
        }

        List<Inventory> inventories =
                adminInventoryRepository.findInventories(search, warehouseId, productId,
                        lowStock, outOfStock, sortBy, direction, page, size);

        long totalElements =
                adminInventoryRepository.countInventories(search, warehouseId, productId, lowStock, outOfStock);

        List<AdminInventoryListResponse> content =
                inventories.stream()
                        .map(this::toListResponse)
                        .toList();

        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new PageResponse<>(
                content,
                page,
                size,
                totalElements,
                totalPages
        );
    }

    @Override
    @RolesAllowed("ADMIN")
    @Transactional(Transactional.TxType.SUPPORTS)
    public AdminInventoryDetailsResponse getInventoryDetails(UUID inventoryId) {
        if (inventoryId == null) {
            throw new IllegalArgumentException("Inventory ID cannot be null");
        }

        Inventory inventory = adminInventoryRepository.findDetailsById(inventoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Inventory " + inventoryId + " not found"));

        return toDetailsResponse(inventory);
    }

    @Override
    @RolesAllowed("ADMIN")
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<WarehouseOptionResponse> getWarehouseOptions() {
        return adminInventoryRepository.findWarehouseOptions()
                .stream()
                .map(warehouse ->
                        new WarehouseOptionResponse(
                                warehouse.getId(),
                                warehouse.getName())
                ).toList();
    }

    @Override
    @RolesAllowed("ADMIN")
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<ProductOptionResponse> getProductOptions(String search) {
        return adminInventoryRepository
                .findProductOptions(search)
                .stream()
                .map(product ->
                        new ProductOptionResponse(
                                product.getId(),
                                product.getTitle())
                ).toList();
    }

    private AdminInventoryListResponse toListResponse(Inventory inventory) {
        int available = inventory.getAvailableQuantity();
        boolean outOfStock = available <= 0;

        boolean lowStock = available <= inventory.getReorderLevel();

        return new AdminInventoryListResponse(
                inventory.getId(),
                inventory.getInventoryNumber(),
                inventory.getProduct().getId(),
                inventory.getProduct().getTitle(),
                inventory.getWarehouse().getId(),
                inventory.getWarehouse().getName(),
                inventory.getQuantity(),
                inventory.getReservedQuantity(),
                available,
                inventory.getReorderLevel(),
                lowStock,
                outOfStock,
                inventory.getStatus()
        );
    }

    private AdminInventoryDetailsResponse toDetailsResponse(Inventory inventory) {

        int available = inventory.getAvailableQuantity();

        boolean outOfStock = available <= 0;
        boolean lowStock = available <= inventory.getReorderLevel();

        return new AdminInventoryDetailsResponse(
                inventory.getId(),
                inventory.getInventoryNumber(),
                inventory.getProduct().getId(),
                inventory.getProduct().getTitle(),
                inventory.getWarehouse().getId(),
                inventory.getWarehouse().getName(),
                inventory.getQuantity(),
                inventory.getReservedQuantity(),
                available,
                inventory.getReorderLevel(),
                lowStock,
                outOfStock,

                inventory.getStatus(),
                inventory.getSellingPrice(),
                inventory.getGrnItem().getUnitCost(),

                inventory.getGrnItem().getGrn().getVendor().getContactFirstName() +" "+inventory.getGrnItem().getGrn().getVendor().getContactLastName(),
                inventory.getGrnItem().getGrn().getVendor().getCompany().getName(),

                inventory.getGrnItem().getCreatedAt()
        );
    }
}
