package com.globaltrade.logistics.core.dto.customer.dashboard;

import java.math.BigDecimal;

public record DashboardSummaryResponse(
        long totalOrders,
        long pendingOrders,
        long confirmedOrders,
        long activeShipments,
        long deliveredShipments,
        BigDecimal totalSpent
) {
}
