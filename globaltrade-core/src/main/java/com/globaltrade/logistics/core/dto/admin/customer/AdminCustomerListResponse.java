package com.globaltrade.logistics.core.dto.admin.customer;

import com.globaltrade.logistics.core.entity.customer.CustomerStatus;
import com.globaltrade.logistics.core.entity.customer.CustomerType;

import java.util.UUID;

public record AdminCustomerListResponse(
        UUID customerId,
        String customerNumber,

        UUID companyId,
        String companyName,

        String firstName,
        String lastName,
        String email,
        String mobile1,

        CustomerType customerType,
        CustomerStatus status,
        boolean kycVerified
) {
}
