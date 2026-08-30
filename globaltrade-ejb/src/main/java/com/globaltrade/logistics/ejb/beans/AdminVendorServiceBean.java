package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.dto.admin.order.PageResponse;
import com.globaltrade.logistics.core.dto.admin.vendor.AdminVendorDetailsResponse;
import com.globaltrade.logistics.core.dto.admin.vendor.AdminVendorListResponse;
import com.globaltrade.logistics.core.entity.vendor.Vendor;
import com.globaltrade.logistics.core.entity.vendor.VendorStatus;
import com.globaltrade.logistics.core.exception.VendorServiceException;
import com.globaltrade.logistics.core.service.AdminVendorService;
import com.globaltrade.logistics.ejb.repository.AdminVendorRepository;
import com.globaltrade.logistics.ejb.repository.VendorRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Stateless
public class AdminVendorServiceBean implements AdminVendorService {

    @Inject
    private AdminVendorRepository adminVendorRepository;
    @Inject
    private VendorRepository vendorRepository;

    @Override
    @RolesAllowed({"ADMIN","OPERATIONS_MANAGER"})
    @Transactional(Transactional.TxType.SUPPORTS)
    public PageResponse<AdminVendorListResponse> getVendors(String search, UUID companyId, VendorStatus status, String sortBy, String direction, int page, int size) {
        if (page < 0) {
            throw new VendorServiceException("Page cannot be negative");
        }
        if (size < 1 || size > 100) {
            throw new VendorServiceException("Size must be between 1 and 100");
        }

        List<Vendor> vendors = adminVendorRepository
                .findVendors(search, companyId, status, sortBy, direction, page, size);

        long totalElements = adminVendorRepository.countVendors(search, companyId, status);

        List<AdminVendorListResponse> content = vendors.stream()
                .map(this::toListResponse)
                .toList();

        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new PageResponse<>(
                content,
                page,
                size,
                totalElements,
                totalPages
        );

    }

    @Override
    @RolesAllowed({"ADMIN","OPERATIONS_MANAGER"})
    @Transactional(Transactional.TxType.SUPPORTS)
    public AdminVendorDetailsResponse getVendorDetails(UUID vendorId) {
        Optional<Vendor> optionalVendor = adminVendorRepository.findByIdWithDetails(vendorId);
        if (optionalVendor.isEmpty()) {
            throw new VendorServiceException("Vendor not found");
        }
        Vendor vendor = optionalVendor.get();


        Object[] vendorGRNSummery = adminVendorRepository.getVendorGRNSummery(vendorId);

        long totalGRNs = ((Number) vendorGRNSummery[0]).longValue();
        BigDecimal totalPurchasedValue = (BigDecimal) vendorGRNSummery[1];

        LocalDateTime lastGRNDate = (LocalDateTime) vendorGRNSummery[2];

        return toAdminVendorDetailsResponse(vendor, totalGRNs, totalPurchasedValue, lastGRNDate);
    }

    @Override
    @RolesAllowed("ADMIN")
    @Transactional(Transactional.TxType.REQUIRED)
    public AdminVendorDetailsResponse activateVendor(UUID vendorId) {
        Vendor vendor = vendorRepository
                .findById(vendorId).orElseThrow(() -> new VendorServiceException("Vendor not found"));

        if (vendor.getStatus() == VendorStatus.ACTIVE) {
            throw new VendorServiceException("Vendor is already active.");
        }

        vendor.setStatus(VendorStatus.ACTIVE);
        return toAdminVendorDetailsResponse(vendor, 0, BigDecimal.ZERO, null);
    }

    @Override
    @RolesAllowed("ADMIN")
    @Transactional(Transactional.TxType.REQUIRED)
    public AdminVendorDetailsResponse deactivateVendor(UUID vendorId) {
        Vendor vendor = vendorRepository
                .findById(vendorId).orElseThrow(() -> new VendorServiceException("Vendor not found"));

        if (vendor.getStatus() == VendorStatus.INACTIVE) {
            throw new VendorServiceException("Vendor is already Inactive.");
        }
        vendor.setStatus(VendorStatus.INACTIVE);
        return toAdminVendorDetailsResponse(vendor, 0, BigDecimal.ZERO, null);
    }

    private AdminVendorListResponse toListResponse(Vendor vendor) {
        return new AdminVendorListResponse(
                vendor.getId(),
                vendor.getVendorNumber(),
                vendor.getCompany().getName(),
                vendor.getContactFirstName() + " " + vendor.getContactLastName(),
                vendor.getPerformanceScore(),
                vendor.getStatus()
        );
    }

    private AdminVendorDetailsResponse toAdminVendorDetailsResponse(Vendor vendor, long totalGRNs, BigDecimal totalPurchasedValue, LocalDateTime lastGRNDate) {
        return new AdminVendorDetailsResponse(
                vendor.getId(),
                vendor.getVendorNumber(),
                vendor.getCreatedAt(),

                vendor.getCompany().getId(),
                vendor.getCompany().getName(),

                vendor.getContactFirstName(),
                vendor.getContactLastName(),

                vendor.getEmail(),
                vendor.getMobile1(),
                vendor.getMobile2(),

                totalGRNs,
                totalPurchasedValue,
                lastGRNDate,

                vendor.getPerformanceScore(),
                vendor.getStatus());
    }
}
