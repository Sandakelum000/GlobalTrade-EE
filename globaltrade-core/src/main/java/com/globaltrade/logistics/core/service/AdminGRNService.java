package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.admin.grn.AdminGRNDetailsResponse;
import com.globaltrade.logistics.core.dto.admin.grn.AdminGRNListResponse;
import com.globaltrade.logistics.core.dto.admin.order.PageResponse;
import com.globaltrade.logistics.core.dto.common.CompanyResponse;
import com.globaltrade.logistics.core.dto.common.VendorOptionResponse;
import com.globaltrade.logistics.core.dto.grn.GRNRegistrationRequest;
import com.globaltrade.logistics.core.dto.grn.GRNRegistrationResponse;
import com.globaltrade.logistics.core.entity.grn.GRNStatus;
import jakarta.ejb.Local;

import java.util.List;
import java.util.UUID;

@Local
public interface AdminGRNService {
    GRNRegistrationResponse createGRN(GRNRegistrationRequest request);
    GRNRegistrationResponse cancelGRN(UUID grnId);
    PageResponse<AdminGRNListResponse> getGRNs(
            String search,
            UUID companyId,
            UUID warehouseId,
            GRNStatus status,
            String sortBy,
            String direction,
            int page,
            int size
    );
    AdminGRNDetailsResponse getGRNDetails(UUID grnId);
    List<CompanyResponse> getVendorCompanyOptions();
    List<VendorOptionResponse> getVendorsByCompanyId(UUID companyId);
}
