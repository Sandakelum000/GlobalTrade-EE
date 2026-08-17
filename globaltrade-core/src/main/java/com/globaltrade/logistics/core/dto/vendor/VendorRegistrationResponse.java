package com.globaltrade.logistics.core.dto.vendor;

import com.globaltrade.logistics.core.entity.vendor.VendorStatus;

import java.util.UUID;

public record VendorRegistrationResponse(
        UUID vendorId,
        UUID companyId,
        String companyName,
        String contactFirstName,
        String contactLastName,
        String email,
        String mobile1,
        String mobile2,
        String username,
        VendorStatus status
) {
}
