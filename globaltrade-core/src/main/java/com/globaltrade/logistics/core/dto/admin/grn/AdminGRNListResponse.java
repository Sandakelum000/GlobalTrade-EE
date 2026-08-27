package com.globaltrade.logistics.core.dto.admin.grn;

import com.globaltrade.logistics.core.entity.grn.GRNStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AdminGRNListResponse (
        UUID grnId,
        String grnNumber,

        String vendorName,
        String vendorCompany,
        String country,

        String warehouseName,

        int itemCount,
        GRNStatus status,
        LocalDateTime receivedAt
){
}
