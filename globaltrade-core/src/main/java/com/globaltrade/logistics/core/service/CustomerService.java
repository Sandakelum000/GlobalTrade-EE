package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.customer.CustomerRegistrationRequest;
import com.globaltrade.logistics.core.dto.customer.CustomerRegistrationResponse;
import com.globaltrade.logistics.core.entity.customer.Customer;
import jakarta.ejb.Local;

@Local
public interface CustomerService {
    CustomerRegistrationResponse registerCustomer(CustomerRegistrationRequest request);
}
