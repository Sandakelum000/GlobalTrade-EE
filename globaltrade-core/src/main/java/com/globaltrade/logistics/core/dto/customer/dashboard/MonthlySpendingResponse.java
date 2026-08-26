package com.globaltrade.logistics.core.dto.customer.dashboard;

import java.math.BigDecimal;

public record MonthlySpendingResponse(
        String month,
        BigDecimal amount
){
}
