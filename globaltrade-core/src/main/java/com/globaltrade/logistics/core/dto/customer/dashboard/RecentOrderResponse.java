package com.globaltrade.logistics.core.dto.customer.dashboard;

import com.globaltrade.logistics.core.entity.order.OrderStatus;
import com.globaltrade.logistics.core.entity.payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record RecentOrderResponse(
        UUID orderId,
        String orderNumber,
        LocalDateTime createdAt,
        BigDecimal totalAmount,
        OrderStatus status
) {
}
