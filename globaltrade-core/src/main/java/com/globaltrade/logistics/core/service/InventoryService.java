package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.inventory.InventoryMonitorRecord;
import com.globaltrade.logistics.core.entity.warehouse.Inventory;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface InventoryService {
    List<InventoryMonitorRecord> findLowStock();
}
