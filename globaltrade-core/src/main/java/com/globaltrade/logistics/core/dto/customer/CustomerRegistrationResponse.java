package com.globaltrade.logistics.core.dto.customer;

import com.globaltrade.logistics.core.entity.customer.CustomerStatus;
import com.globaltrade.logistics.core.entity.customer.CustomerType;

import java.util.UUID;

public record CustomerRegistrationResponse(
        UUID id,
        String username,
        String firstName,
        String lastName,
        String email,
        String mobile1,
        String mobile2,
        UUID companyId,
        CustomerType customerType,
        CustomerStatus status,
        boolean kycVerified
) {
}
