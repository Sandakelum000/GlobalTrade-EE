package com.globaltrade.logistics.core.dto.admin.order;

import com.globaltrade.logistics.core.entity.common.Address;
import com.globaltrade.logistics.core.entity.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AdminOrderDetailsResponse(
        UUID orderId,
        String orderNumber,
        OrderStatus status,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        AdminCustomerResponse customer,
        Address shippingAddress,
        List<AdminOrderItemResponse> items,
        List<AdminShipmentResponse> shipments
) {
}
