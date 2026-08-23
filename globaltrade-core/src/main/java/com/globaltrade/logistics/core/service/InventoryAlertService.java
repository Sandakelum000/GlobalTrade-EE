package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.inventory.InventoryMonitorRecord;
import jakarta.ejb.Local;

@Local
public interface InventoryAlertService {
    void handleLowStock(InventoryMonitorRecord record);
}
