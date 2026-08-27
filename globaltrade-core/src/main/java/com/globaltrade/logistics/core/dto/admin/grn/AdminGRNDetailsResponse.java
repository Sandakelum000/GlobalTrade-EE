package com.globaltrade.logistics.core.dto.admin.grn;

import com.globaltrade.logistics.core.entity.grn.GRNStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AdminGRNDetailsResponse(
        UUID grnId,
        String grnNumber,

        UUID vendorId,
        String vendorName,

        UUID warehouseId,
        String warehouseName,

        GRNStatus status,
        LocalDateTime receivedAt,

        List<AdminGRNItemResponse> items
) {
}
