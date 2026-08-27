package com.globaltrade.logistics.core.dto.admin.grn;

import java.math.BigDecimal;
import java.util.UUID;

public record AdminGRNItemResponse(
        UUID id,
        UUID productId,
        String productName,
        Integer quantity,
        BigDecimal unitCost,
        BigDecimal totalCost
) {
}
