package com.globaltrade.logistics.core.dto.order;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID id,
        String productNumber,
        String productTitle,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice
) {
}
