package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.entity.vendor.Vendor;
import com.globaltrade.logistics.core.service.VendorPerformanceService;
import com.globaltrade.logistics.ejb.repository.GRNRepository;
import com.globaltrade.logistics.ejb.repository.VendorRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@Stateless
public class VendorPerformanceServiceBean implements VendorPerformanceService {
    @Inject
    private VendorRepository vendorRepository;
    @Inject
    private GRNRepository grnRepository;

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public void evaluatePerformance() {
        List<Vendor> activeVendors = vendorRepository.findActiveVendors();

        for (Vendor vendor : activeVendors) {
            long receivedGRN = grnRepository.receivedCountByVendor(vendor.getId());

        }
    }
}
