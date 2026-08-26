package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.customer.dashboard.CustomerDashboardResponse;

import java.util.UUID;

public interface CustomerDashboardService {
    CustomerDashboardResponse getDashboard(UUID customerId);
}
