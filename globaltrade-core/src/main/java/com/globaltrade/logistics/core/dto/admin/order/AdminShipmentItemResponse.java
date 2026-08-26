package com.globaltrade.logistics.core.dto.admin.order;

import com.globaltrade.logistics.core.entity.order.shipment.ShipmentItemStatus;

import java.util.UUID;

public record AdminShipmentItemResponse(
        UUID shipmentItemId,
        UUID orderItemId,
        UUID productId,
        String productName,
        int quantity,
        ShipmentItemStatus status
) {
}
