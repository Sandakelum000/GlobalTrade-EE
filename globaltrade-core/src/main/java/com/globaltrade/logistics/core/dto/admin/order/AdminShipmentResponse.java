package com.globaltrade.logistics.core.dto.admin.order;

import com.globaltrade.logistics.core.entity.common.Address;
import com.globaltrade.logistics.core.entity.order.shipment.ShipmentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AdminShipmentResponse (
        UUID shipmentId,
        String shipmentNumber,
        UUID warehouseId,
        String warehouseName,
        ShipmentStatus status,
        Address originAddress,
        Address destinationAddress,
        LocalDateTime shippedAt,
        LocalDateTime estimatedDeliveryDate,
        LocalDateTime deliveredAt,
        List<AdminShipmentItemResponse> items,
        List<AdminTrackingResponse> tracking
){
}
