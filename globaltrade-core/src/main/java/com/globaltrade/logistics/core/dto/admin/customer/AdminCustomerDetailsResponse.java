package com.globaltrade.logistics.core.dto.admin.customer;

import com.globaltrade.logistics.core.entity.customer.CustomerStatus;
import com.globaltrade.logistics.core.entity.customer.CustomerType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AdminCustomerDetailsResponse(
        UUID customerId,
        String customerNumber,
        LocalDateTime joinedAt,

        UUID companyId,
        String companyName,

        String firstName,
        String lastName,
        String email,
        String mobile1,
        String mobile2,

        CustomerType customerType,
        CustomerStatus status,
        boolean kycVerified,

        long totalOrders,
        BigDecimal totalOrderValue,
        LocalDateTime lastOrderDate
) {
}
