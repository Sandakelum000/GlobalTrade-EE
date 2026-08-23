package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.inventory.InventoryMonitorRecord;
import com.globaltrade.logistics.core.entity.warehouse.Inventory;
import jakarta.ejb.Local;

import java.util.List;
import java.util.UUID;

@Local
public interface InventoryService {
    List<InventoryMonitorRecord> findLowStock();
}
