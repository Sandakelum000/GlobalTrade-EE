package com.globaltrade.logistics.core.dto.product;

import java.util.UUID;

public record ProductRegistrationResponse(
        UUID productId,
        String productNumber,
        String productTitle,
        String description,
        Integer recorderLevel
) {
}
