package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.payment.PaymentRegistrationRequest;
import jakarta.ejb.Local;

@Local
public interface CheckoutMessageService {
    void sendPayload(PaymentRegistrationRequest payload);
}
