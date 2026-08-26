package com.globaltrade.logistics.core.dto.admin.order;

import java.util.UUID;

public record AdminCustomerResponse(
        UUID customerId,
        String firstName,
        String lastName,
        String email,
        String mobile1,
        String mobile2,
        String username
) {
}
