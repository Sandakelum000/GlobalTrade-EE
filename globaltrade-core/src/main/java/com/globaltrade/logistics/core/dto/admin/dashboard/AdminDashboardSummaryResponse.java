package com.globaltrade.logistics.core.dto.admin.dashboard;

import java.math.BigDecimal;

public record AdminDashboardSummaryResponse(
        long totalOrders,
        long pendingOrders,
        long confirmedOrders,
        long cancelledOrders,
        long deliveredOrders,
        long activeShipments,
        long deliveredShipments,
        long totalCustomers,
        long totalVendors,
        long totalProducts,
        long totalWarehouses,
        long lowStockItems,
        BigDecimal totalRevenue
){
}
