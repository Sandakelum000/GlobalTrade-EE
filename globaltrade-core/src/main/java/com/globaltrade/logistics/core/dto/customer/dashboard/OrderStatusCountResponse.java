package com.globaltrade.logistics.core.dto.customer.dashboard;

import com.globaltrade.logistics.core.entity.order.OrderStatus;

public record OrderStatusCountResponse (
        OrderStatus status,
        long count
){
}
