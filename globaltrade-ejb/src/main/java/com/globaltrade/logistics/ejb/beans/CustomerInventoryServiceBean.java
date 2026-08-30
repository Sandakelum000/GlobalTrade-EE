package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.dto.inventory.dashboard.CustomerInventoryResponse;
import com.globaltrade.logistics.core.entity.warehouse.Inventory;
import com.globaltrade.logistics.core.service.CustomerInventoryService;
import com.globaltrade.logistics.ejb.repository.InventoryRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;

import java.util.List;

@Stateless
public class CustomerInventoryServiceBean implements CustomerInventoryService {

    @Inject
    private InventoryRepository inventoryRepository;

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed({"ADMIN","CUSTOMER"})
    public List<CustomerInventoryResponse> getAvailableInventory() {
        return inventoryRepository.findAvailableInventory()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private CustomerInventoryResponse toResponse(Inventory inventory) {
        return new CustomerInventoryResponse(
                inventory.getId(),
                inventory.getProduct().getTitle(),
                inventory.getWarehouse().getName() + " " + inventory.getWarehouse().getAddress().getCity(),
                inventory.getAvailableQuantity(),
                inventory.getSellingPrice(),
                inventory.getCreatedAt());
    }
}
