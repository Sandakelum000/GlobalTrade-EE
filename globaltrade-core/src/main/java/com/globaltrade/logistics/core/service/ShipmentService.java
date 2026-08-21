package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.shipment.*;

import java.util.List;
import java.util.UUID;

public interface ShipmentService {
    ShipmentRegistrationResponse createShipment(ShipmentRegistrationRequest shipmentRegistrationRequest);
    ShipmentTrackingResponse updateTracking(UUID shipmentId, ShipmentTrackingRequest request);
    ShipmentRegistrationResponse shipShipment(UUID shipmentId);
    ShipmentRegistrationResponse deliverShipment(UUID shipmentId);
    List<ShipmentMonitorRecord> findActiveShipments();
}
