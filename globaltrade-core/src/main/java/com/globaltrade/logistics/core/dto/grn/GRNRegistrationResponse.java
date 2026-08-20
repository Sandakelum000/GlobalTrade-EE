package com.globaltrade.logistics.core.dto.grn;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GRNRegistrationResponse(
        UUID grnId,
        String grnNumber,
        String vendorNumber,
        String vendorName,
        String warehouseName,
        LocalDateTime receiveAt,
        String status,
        BigDecimal totalCost,
        List<GRNItemResponse> items
) implements AuditableResponse{
    @Override
    public UUID getEntityId() {
        return grnId;
    }
}
