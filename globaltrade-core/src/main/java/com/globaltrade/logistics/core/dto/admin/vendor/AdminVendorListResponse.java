package com.globaltrade.logistics.core.dto.admin.vendor;

import com.globaltrade.logistics.core.entity.vendor.VendorStatus;

import java.util.UUID;

public record AdminVendorListResponse(
        UUID vendorId,
        String vendorNumber,
        String companyName,
        String vendorName,
        Integer performanceScore,
        VendorStatus status
) {
}
