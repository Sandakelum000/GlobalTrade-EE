package com.globaltrade.logistics.core.dto.admin.order;

import com.globaltrade.logistics.core.entity.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AdminOrderListResponse(
        UUID orderId,
        String orderNumber,
        UUID customerId,
        String customerName,
        String customerEmail,
        OrderStatus status,
        BigDecimal totalAmount,
        int itemCount,
        int shipmentCount,
        LocalDateTime createdAt
) {
}
