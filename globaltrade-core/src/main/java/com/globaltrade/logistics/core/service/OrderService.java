package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.order.OrderRegistrationRequest;
import com.globaltrade.logistics.core.dto.order.OrderRegistrationResponse;
import jakarta.ejb.Local;

import java.util.UUID;

@Local
public interface OrderService {
    OrderRegistrationResponse createOrder(OrderRegistrationRequest request,UUID customerId);
    OrderRegistrationResponse cancelOrder(UUID orderId);
}
