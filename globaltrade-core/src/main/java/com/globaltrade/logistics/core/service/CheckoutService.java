package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.entity.payment.PayHereDTO;
import jakarta.ejb.Local;

import java.util.UUID;

@Local
public interface CheckoutService {
    PayHereDTO processCheckout(UUID orderId);
}
