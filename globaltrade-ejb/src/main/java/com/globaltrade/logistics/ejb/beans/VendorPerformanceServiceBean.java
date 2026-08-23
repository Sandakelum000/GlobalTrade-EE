package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.entity.vendor.Vendor;
import com.globaltrade.logistics.core.service.VendorPerformanceService;
import com.globaltrade.logistics.ejb.repository.GRNRepository;
import com.globaltrade.logistics.ejb.repository.VendorRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.logging.Logger;

@Stateless
public class VendorPerformanceServiceBean implements VendorPerformanceService {
    private static final Logger LOGGER = Logger.getLogger(VendorPerformanceServiceBean.class.getName());
    @Inject
    private VendorRepository vendorRepository;
    @Inject
    private GRNRepository grnRepository;

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public void evaluatePerformance() {
        List<Vendor> activeVendors = vendorRepository.findActiveVendors();

        for (Vendor vendor : activeVendors) {
            long totalGRNs = grnRepository.countByVendor(vendor.getId());
            long successfulGRNs = grnRepository.successfulCountByVendor(vendor.getId());

            int score = calculatePerformance(totalGRNs, successfulGRNs);
            vendor.setPerformanceScore(score);

            LOGGER.info( "Vendor performance: " + vendor.getVendorNumber() + " | Total GRNs: " + totalGRNs + " | Successful: " + successfulGRNs + " | Score: " + score + "%");
        }
    }

    private int calculatePerformance(long totalGRNs, long successfulGRNs) {
        if(totalGRNs == 0) return 0;
        return (int) Math.round(((double) successfulGRNs / totalGRNs) * 100);
    }
}
