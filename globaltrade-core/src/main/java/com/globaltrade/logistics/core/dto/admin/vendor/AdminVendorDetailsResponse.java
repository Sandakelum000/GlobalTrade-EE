package com.globaltrade.logistics.core.dto.admin.vendor;

import com.globaltrade.logistics.core.entity.vendor.VendorStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AdminVendorDetailsResponse(
        UUID vendorId,
        String vendorNumber,
        LocalDateTime joinedAt,

        UUID companyId,
        String companyName,

        String contactFirstName,
        String contactLastName,

        String email,
        String mobile1,
        String mobile2,

        long totalGRNs,
        BigDecimal totalPurchasedValue,
        LocalDateTime lastGRNDate,

        Integer performanceScore,
        VendorStatus status
) {
}
