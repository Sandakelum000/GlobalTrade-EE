package com.globaltrade.logistics.core.dto.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Username is required")
        @Size(max = 30, message = "Username cannot be exceed to 30 characters")
        String username,

        @NotBlank(message = "Password is required")
        @Size(max = 50, message = "Username cannot be exceed to 50 characters")
        String password
) {
}
