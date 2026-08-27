package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.shipment.*;

import java.util.List;
import java.util.UUID;

public interface ShipmentService {
    void createShipmentsForOrder(UUID orderId);
    ShipmentRegistrationResponse shipShipment(UUID shipmentId);
    ShipmentTrackingResponse updateShipmentTracking(UUID shipmentId, ShipmentTrackingRequest request);
    List<ShipmentMonitorRecord> findActiveShipments();
}
