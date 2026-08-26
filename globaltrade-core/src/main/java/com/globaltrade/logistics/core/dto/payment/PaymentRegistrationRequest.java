package com.globaltrade.logistics.core.dto.payment;

import com.globaltrade.logistics.core.entity.payment.PaymentMethod;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.UUID;

public record PaymentRegistrationRequest(
        UUID orderId,
        int statusCode
) implements Serializable {
}
