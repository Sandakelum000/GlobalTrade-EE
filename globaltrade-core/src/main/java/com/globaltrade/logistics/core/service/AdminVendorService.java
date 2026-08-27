package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.admin.order.PageResponse;
import com.globaltrade.logistics.core.dto.admin.vendor.AdminVendorDetailsResponse;
import com.globaltrade.logistics.core.dto.admin.vendor.AdminVendorListResponse;
import com.globaltrade.logistics.core.entity.vendor.VendorStatus;
import jakarta.ejb.Local;

import java.util.UUID;

@Local
public interface AdminVendorService {
    PageResponse<AdminVendorListResponse> getVendors(String search, UUID companyId, VendorStatus status,
                                                     String sortBy, String direction, int page, int size);

    AdminVendorDetailsResponse getVendorDetails(UUID vendorId);
    AdminVendorDetailsResponse activateVendor(UUID vendorId);
    AdminVendorDetailsResponse deactivateVendor(UUID vendorId);
}
