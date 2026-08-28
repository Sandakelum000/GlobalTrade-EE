package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.admin.customer.AdminCustomerDetailsResponse;
import com.globaltrade.logistics.core.dto.admin.customer.AdminCustomerListResponse;
import com.globaltrade.logistics.core.dto.admin.order.PageResponse;
import com.globaltrade.logistics.core.dto.common.CompanyResponse;
import com.globaltrade.logistics.core.entity.customer.CustomerStatus;
import com.globaltrade.logistics.core.entity.customer.CustomerType;
import jakarta.ejb.Local;

import java.util.List;
import java.util.UUID;

@Local
public interface AdminCustomerService {
    PageResponse<AdminCustomerListResponse> getCustomers(
            String search,
            UUID companyId,
            CustomerType customerType,
            CustomerStatus status,
            Boolean kycVerified,
            String sortBy,
            String direction,
            int page,
            int size
    );

    AdminCustomerDetailsResponse getCustomerDetails(
            UUID customerId
    );

    List<CompanyResponse> getCustomerCompanies();

    interface AuditableResponse {
        UUID getEntityId();
    }
}
