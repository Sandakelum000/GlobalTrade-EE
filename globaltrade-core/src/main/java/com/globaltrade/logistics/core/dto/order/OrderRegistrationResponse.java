package com.globaltrade.logistics.core.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderRegistrationResponse(
        UUID orderId,
        String orderNumber,
        String customerNumber,
        String customerName,
        String status,
        LocalDateTime orderDate,
        BigDecimal totalAmount,
        List<OrderItemResponse> items
) {
}
