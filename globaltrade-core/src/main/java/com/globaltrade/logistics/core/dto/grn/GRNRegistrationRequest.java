package com.globaltrade.logistics.core.dto.grn;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record GRNRegistrationRequest(
        @NotNull(message = "Vendor is required")
        UUID vendorId,

        @NotNull(message = "Warehouse is required")
        UUID warehouseId,

        @NotEmpty(message = "At least one GRN item is required")
        @Valid
        List<GRNItemRequest> items
) {
}
