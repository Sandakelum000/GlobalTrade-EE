package com.globaltrade.logistics.core.dto.login;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
        @NotBlank(message = "Refresh token must not be null")
        String refreshToken) {
}
