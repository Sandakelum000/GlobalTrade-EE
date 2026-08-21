package com.globaltrade.logistics.core.dto.customs;

import com.globaltrade.logistics.core.entity.order.shipment.CustomsDocumentType;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomsDocumentResponse(
        UUID id,
        String documentNumber,
        CustomsDocumentType documentType,
        String fileName,
        String filePath,
        String shipmentNumber,
        LocalDateTime issuedAt
) {
}
