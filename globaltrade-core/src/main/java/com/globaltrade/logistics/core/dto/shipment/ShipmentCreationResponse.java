package com.globaltrade.logistics.core.dto.shipment;

import com.globaltrade.logistics.core.service.AuditableResponse;

import java.util.List;
import java.util.UUID;

public record ShipmentCreationResponse(
        UUID orderId,
        String orderNumber,
        List<ShipmentRegistrationResponse> shipments
) implements AuditableResponse {
    @Override
    public UUID getEntityId() {
        return orderId;
    }
}
