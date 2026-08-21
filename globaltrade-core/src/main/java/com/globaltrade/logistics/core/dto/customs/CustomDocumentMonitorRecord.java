package com.globaltrade.logistics.core.dto.customs;

import com.globaltrade.logistics.core.entity.order.shipment.CustomsDocumentType;
import com.globaltrade.logistics.core.entity.order.shipment.ShipmentStatus;

import java.util.List;
import java.util.UUID;

public record CustomDocumentMonitorRecord(
        UUID shipmentId,
        String shipNumber,
        ShipmentStatus shipmentStatus,
        List<CustomsDocumentType> missingDocuments
) {
}
