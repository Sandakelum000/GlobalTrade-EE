package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.product.ProductRegistrationRequest;
import com.globaltrade.logistics.core.dto.product.ProductRegistrationResponse;
import jakarta.ejb.Local;

@Local
public interface ProductService {
    ProductRegistrationResponse registerProduct(ProductRegistrationRequest productRegistrationRequest);
}
