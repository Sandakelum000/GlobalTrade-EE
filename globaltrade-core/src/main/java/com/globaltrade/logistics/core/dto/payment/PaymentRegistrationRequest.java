package com.globaltrade.logistics.core.dto.payment;

import com.globaltrade.logistics.core.entity.payment.PaymentMethod;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PaymentRegistrationRequest(
        @NotNull(message = "OrderId is required")
        UUID orderId,
        @NotNull(message = "payment method is required")
        PaymentMethod paymentMethod
) {
}
