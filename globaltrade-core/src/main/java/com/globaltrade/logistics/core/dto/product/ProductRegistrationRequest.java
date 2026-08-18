package com.globaltrade.logistics.core.dto.product;

import jakarta.validation.constraints.*;

public record ProductRegistrationRequest (
        @NotBlank(message = "Product title is required")
        @Size(max = 150, message = "Product title must not exceed 150 characters")
        String title,

        @Size(max = 500, message = "Product description must not exceed 500 characters")
        String description,

        @NotNull(message = "Reorder level is required")
        @Min(value = 0, message = "Reorder level cannot be negative")
        Integer reorderLevel
){
}
