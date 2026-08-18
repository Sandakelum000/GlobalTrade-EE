package com.globaltrade.logistics.core.dto.grn;

import java.math.BigDecimal;
import java.util.UUID;

public record GRNItemResponse(
        UUID grnItemId,
        String productNumber,
        String productTitle,
        Integer quantity,
        BigDecimal unitCost,
        BigDecimal totalCost
){
}
