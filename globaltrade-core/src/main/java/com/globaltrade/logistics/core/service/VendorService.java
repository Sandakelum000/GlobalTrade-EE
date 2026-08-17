package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.vendor.VendorRegistrationRequest;
import com.globaltrade.logistics.core.dto.vendor.VendorRegistrationResponse;
import jakarta.ejb.Local;

@Local
public interface VendorService {
    VendorRegistrationResponse registerVendor(VendorRegistrationRequest request);
}
