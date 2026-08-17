package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.dto.vendor.VendorRegistrationRequest;
import com.globaltrade.logistics.core.dto.vendor.VendorRegistrationResponse;
import com.globaltrade.logistics.core.entity.company.Company;
import com.globaltrade.logistics.core.entity.security.Role;
import com.globaltrade.logistics.core.entity.security.RoleType;
import com.globaltrade.logistics.core.entity.security.User;
import com.globaltrade.logistics.core.entity.vendor.Vendor;
import com.globaltrade.logistics.core.exception.UsernameAlreadyExistsException;
import com.globaltrade.logistics.core.service.VendorService;
import com.globaltrade.logistics.ejb.repository.CompanyRepository;
import com.globaltrade.logistics.ejb.repository.RoleRepository;
import com.globaltrade.logistics.ejb.repository.UserRepository;
import com.globaltrade.logistics.ejb.repository.VendorRepository;
import com.globaltrade.logistics.ejb.security.PasswordService;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@Stateless
public class VendorServiceBean implements VendorService {

    @Inject
    private VendorRepository vendorRepository;

    @Inject
    private CompanyRepository companyRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private PasswordService passwordService;

    @Inject
    private RoleRepository roleRepository;

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public VendorRegistrationResponse registerVendor(VendorRegistrationRequest request) {

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new UsernameAlreadyExistsException(request.username());
        }

        Company vendorCompany = companyRepository.findById(request.companyId())
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        Role vendorRole = roleRepository.findByName(RoleType.VENDOR)
                .orElseThrow(() -> new IllegalArgumentException("Vendor role not configured"));

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(passwordService.hashPassword(request.password()))
                .build();
        user.getRoles().add(vendorRole);
        userRepository.save(user);

        Vendor vendor = Vendor.builder()
                .contactFirstName(request.contactFirstName())
                .contactLastName(request.contactLastName())
                .email(request.email())
                .mobile1(request.mobile1())
                .mobile2(request.mobile2())
                .company(vendorCompany)
                .user(user)
                .build();

        vendorRepository.save(vendor);

        return new VendorRegistrationResponse(
          vendor.getId(),
                vendorCompany.getId(),
                vendorCompany.getName(),
                vendor.getContactFirstName(),
                vendor.getContactLastName(),
                vendor.getEmail(),
                vendor.getMobile1(),
                vendor.getMobile2(),
                user.getUsername(),
                vendor.getStatus()
        );
    }
}
