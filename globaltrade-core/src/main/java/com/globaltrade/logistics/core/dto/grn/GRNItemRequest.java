package com.globaltrade.logistics.core.dto.grn;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record GRNItemRequest(
        @NotNull(message = "Product is required")
        UUID productId,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity,

        @NotNull(message = "Unit cost is required")
        @DecimalMin(value = "0.00", message = "Unit cost must be greater than zero")
        BigDecimal unitCost,

        @NotNull(message = "Selling price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Selling price must be greater than zero")
        BigDecimal sellingPrice


) {
}
