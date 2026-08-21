package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.dto.inventory.InventoryMonitorRecord;
import com.globaltrade.logistics.core.entity.warehouse.Inventory;
import com.globaltrade.logistics.core.service.InventoryService;
import com.globaltrade.logistics.ejb.repository.InventoryRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@Stateless
public class InventoryServiceBean implements InventoryService {

    @Inject
    private InventoryRepository inventoryRepository;

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<InventoryMonitorRecord> findLowStock() {
        return inventoryRepository.findLowStock().stream()
                .map(inventory -> new InventoryMonitorRecord(
                       inventory.getId(),
                       inventory.getInventoryNumber(),
                       inventory.getProduct().getId(),
                       inventory.getProduct().getProductNumber(),
                       inventory.getWarehouse().getName(),
                       inventory.getAvailableQuantity(),
                       inventory.getReorderLevel()
               )).toList();
    }
}
