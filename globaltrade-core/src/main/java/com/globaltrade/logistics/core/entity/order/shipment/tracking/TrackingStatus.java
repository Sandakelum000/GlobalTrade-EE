package com.globaltrade.logistics.core.entity.order.shipment.tracking;

public enum TrackingStatus {
    LABEL_CREATED,
    PROCESSING,
    PICKED_UP,
    IN_TRANSIT,
    ARRIVED_AT_PORT,
    CUSTOMS_CLEARANCE,
    CUSTOMS_CLEARED,
    OUT_FOR_DELIVERY,
    DELIVERED
}
