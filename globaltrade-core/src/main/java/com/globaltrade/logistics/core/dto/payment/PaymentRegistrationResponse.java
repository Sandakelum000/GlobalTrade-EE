package com.globaltrade.logistics.core.dto.payment;

import com.globaltrade.logistics.core.entity.payment.PaymentMethod;
import com.globaltrade.logistics.core.entity.payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentRegistrationResponse(
        UUID id,
        String paymentNumber,
        UUID orderId,
        String orderNumber,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        PaymentStatus status,
        LocalDateTime paidAt
) {
}
