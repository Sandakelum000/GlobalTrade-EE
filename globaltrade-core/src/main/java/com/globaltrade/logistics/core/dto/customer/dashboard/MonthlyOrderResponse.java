package com.globaltrade.logistics.core.dto.customer.dashboard;

public record MonthlyOrderResponse(
        String month,
        long count
) {
}
