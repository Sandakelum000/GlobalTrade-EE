package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.payment.PaymentRegistrationRequest;
import com.globaltrade.logistics.core.dto.payment.PaymentRegistrationResponse;
import jakarta.ejb.Local;

import java.util.UUID;

@Local
public interface PaymentService {
    PaymentRegistrationResponse makePayment(UUID orderId,int paymentStatus);
}
