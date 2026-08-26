package com.globaltrade.logistics.core.dto.admin.dashboard;

import com.globaltrade.logistics.core.dto.customer.dashboard.MonthlyOrderResponse;
import com.globaltrade.logistics.core.dto.customer.dashboard.OrderStatusCountResponse;
import com.globaltrade.logistics.core.dto.customer.dashboard.RecentOrderResponse;
import com.globaltrade.logistics.core.dto.customer.dashboard.ShipmentStatusCountResponse;

import java.util.List;

public record AdminDashboardResponse(
        AdminDashboardSummaryResponse summary,
        List<OrderStatusCountResponse> orderStatus,
        List<ShipmentStatusCountResponse> shipmentStatus,
        List<MonthlyOrderResponse> monthlyOrders,
        List<MonthlyRevenueResponse> monthlyRevenue,
        List<RecentOrderResponse> recentOrders,
        List<AdminShipmentResponse> activeShipments,
        List<LowStockResponse> lowStockItems
) {
}
