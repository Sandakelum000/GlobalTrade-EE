package com.globaltrade.logistics.core.dto.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressRequest(
        @NotBlank
        @Size(max = 150)
        String line1,

        @Size(max = 150)
        String line2,

        @Size(max = 150)
        String line3,

        @NotBlank
        @Size(max = 80)
        String city,

        @Size(max = 80)
        String district,

        @Size(max = 80)
        String stateProvince,

        @NotBlank
        @Size(max = 20)
        String postalCode,

        @NotBlank
        @Size(max = 80)
        String country
) {
}
