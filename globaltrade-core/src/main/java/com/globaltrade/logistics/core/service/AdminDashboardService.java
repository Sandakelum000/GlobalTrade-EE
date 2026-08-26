package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.admin.dashboard.AdminDashboardResponse;
import jakarta.ejb.Local;

@Local
public interface AdminDashboardService {
    AdminDashboardResponse getDashboard();
}
