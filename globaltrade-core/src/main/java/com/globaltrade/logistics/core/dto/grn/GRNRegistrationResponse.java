package com.globaltrade.logistics.core.dto.grn;

import com.globaltrade.logistics.core.service.AdminCustomerService;

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
) implements AdminCustomerService.AuditableResponse {
    @Override
    public UUID getEntityId() {
        return grnId;
    }
}
