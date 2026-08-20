package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.shipment.ShipmentRegistrationRequest;
import com.globaltrade.logistics.core.dto.shipment.ShipmentRegistrationResponse;

import java.util.UUID;

public interface ShipmentService {
    ShipmentRegistrationResponse createShipment(ShipmentRegistrationRequest shipmentRegistrationRequest);
    ShipmentRegistrationResponse updateTracking(UUID shipmentId);
    ShipmentRegistrationResponse shipShipment(UUID shipmentId);
    ShipmentRegistrationResponse deliverShipment(UUID shipmentId);
}
