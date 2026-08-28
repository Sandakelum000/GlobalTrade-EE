package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.dto.admin.customer.AdminCustomerDetailsResponse;
import com.globaltrade.logistics.core.dto.admin.customer.AdminCustomerListResponse;
import com.globaltrade.logistics.core.dto.admin.order.PageResponse;
import com.globaltrade.logistics.core.dto.common.CompanyResponse;
import com.globaltrade.logistics.core.entity.company.Company;
import com.globaltrade.logistics.core.entity.customer.Customer;
import com.globaltrade.logistics.core.entity.customer.CustomerStatus;
import com.globaltrade.logistics.core.entity.customer.CustomerType;
import com.globaltrade.logistics.core.exception.CustomerServiceException;
import com.globaltrade.logistics.core.service.AdminCustomerService;
import com.globaltrade.logistics.ejb.repository.AdminInventoryRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Stateless
public class AdminCustomerServiceBean implements AdminCustomerService {

    @Inject
    private AdminCustomerRepository adminCustomerRepository;


    @Override
    @RolesAllowed("ADMIN")
    @Transactional(Transactional.TxType.SUPPORTS)
    public PageResponse<AdminCustomerListResponse> getCustomers(String search, UUID companyId, CustomerType customerType, CustomerStatus status, Boolean kycVerified, String sortBy, String direction, int page, int size) {
        if (page < 0) {
            throw new CustomerServiceException("Page cannot be negative");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("Size must be between 1 and 100");
        }

        List<Customer> customers = adminCustomerRepository.findCustomers(search, companyId, customerType,
                status, kycVerified, sortBy, direction, page, size);

        long totalElements = adminCustomerRepository.countCustomers(search, companyId,
                customerType, status, kycVerified);

        List<AdminCustomerListResponse> content = customers.stream()
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
    @RolesAllowed("ADMIN")
    @Transactional(Transactional.TxType.SUPPORTS)
    public AdminCustomerDetailsResponse getCustomerDetails(UUID customerId) {
        if (customerId == null) {
            throw new CustomerServiceException("Customer id cannot be null");
        }

        Customer customer = adminCustomerRepository.findByIdWithDetails(customerId)
                .orElseThrow(() -> new CustomerServiceException("Customer with id: " + customerId + " not found"));

        long totalOrders =
                adminCustomerRepository.countOrdersByCustomer(customerId);

        BigDecimal totalOrderValue =
                adminCustomerRepository.calculateTotalOrderValue(customerId);

        LocalDateTime lastOrderDate =
                adminCustomerRepository.findLastOrderDate(customerId)
                        .orElse(null);

        Company company = customer.getCompany();

        return new AdminCustomerDetailsResponse(
                customer.getId(),
                customer.getCustomerNumber(),
                customer.getCreatedAt(),

                company.getId(),
                company.getName(),

                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getMobile1(),
                customer.getMobile2(),

                customer.getCustomerType(),
                customer.getStatus(),
                customer.isKycVerified(),

                totalOrders,
                totalOrderValue,
                lastOrderDate
        );
    }

    @Override
    @RolesAllowed("ADMIN")
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<CompanyResponse> getCustomerCompanies() {
        return adminCustomerRepository
                .findCustomerCompanies()
                .stream()
                .map(company -> new CompanyResponse(
                        company.getId(),
                        company.getName()
                ))
                .toList();
    }

    private AdminCustomerListResponse toListResponse(Customer customer) {

        return new AdminCustomerListResponse(
                customer.getId(),
                customer.getCustomerNumber(),

                customer.getCompany().getId(),
                customer.getCompany().getName(),

                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getMobile1(),

                customer.getCustomerType(),
                customer.getStatus(),
                customer.isKycVerified()
        );
    }
}
