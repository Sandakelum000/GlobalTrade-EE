package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.inventory.dashboard.CustomerInventoryResponse;
import jakarta.ejb.Local;

import java.util.List;

@Local
public interface CustomerInventoryService {
    List<CustomerInventoryResponse> getAvailableInventory();
}
