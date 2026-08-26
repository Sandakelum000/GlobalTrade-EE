package com.globaltrade.logistics.core.dto.admin.dashboard;

import java.math.BigDecimal;

public record MonthlyRevenueResponse(
        String month,
        BigDecimal amount
){
}
