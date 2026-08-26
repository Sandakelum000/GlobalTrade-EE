package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.customer.CustomerRegistrationRequest;
import com.globaltrade.logistics.core.dto.customer.CustomerRegistrationResponse;
import com.globaltrade.logistics.core.entity.customer.Customer;
import jakarta.ejb.Local;

import java.util.UUID;

@Local
public interface CustomerService {
    CustomerRegistrationResponse registerCustomer(CustomerRegistrationRequest request);
    UUID findCustomerIdByUsername(String username);
}
