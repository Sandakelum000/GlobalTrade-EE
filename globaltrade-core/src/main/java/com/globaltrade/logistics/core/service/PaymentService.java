package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.payment.PaymentRegistrationRequest;
import com.globaltrade.logistics.core.dto.payment.PaymentRegistrationResponse;
import jakarta.ejb.Local;

@Local
public interface PaymentService {
    PaymentRegistrationResponse makePayment(PaymentRegistrationRequest paymentRegistrationRequest);
}
