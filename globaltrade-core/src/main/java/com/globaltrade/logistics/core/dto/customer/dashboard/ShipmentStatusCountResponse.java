package com.globaltrade.logistics.core.dto.customer.dashboard;

import com.globaltrade.logistics.core.entity.order.shipment.ShipmentStatus;

public record ShipmentStatusCountResponse(
        ShipmentStatus status,
        long count
) {
}
