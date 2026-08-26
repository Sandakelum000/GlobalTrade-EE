package com.globaltrade.logistics.core.dto.customer.dashboard;

import java.util.List;

public record CustomerDashboardResponse(
        DashboardSummaryResponse summary,
        List<RecentOrderResponse> recentOrders,
        List<CustomerShipmentResponse> shipments,
        List<OrderStatusCountResponse> orderStatus,
        List<ShipmentStatusCountResponse> shipmentStatus,
        List<MonthlyOrderResponse> monthlyOrders,
        List<MonthlySpendingResponse> monthlySpending
) {
}
