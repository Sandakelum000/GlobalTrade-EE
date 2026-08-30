package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.dto.customer.CustomerRegistrationRequest;
import com.globaltrade.logistics.core.dto.customer.CustomerRegistrationResponse;
import com.globaltrade.logistics.core.entity.company.Company;
import com.globaltrade.logistics.core.entity.customer.Customer;
import com.globaltrade.logistics.core.entity.security.Role;
import com.globaltrade.logistics.core.entity.security.RoleType;
import com.globaltrade.logistics.core.entity.security.User;
import com.globaltrade.logistics.core.exception.ResourceNotFoundException;
import com.globaltrade.logistics.core.exception.UsernameAlreadyExistsException;
import com.globaltrade.logistics.core.service.CustomerService;
import com.globaltrade.logistics.core.service.NumberSequenceService;
import com.globaltrade.logistics.ejb.repository.CompanyRepository;
import com.globaltrade.logistics.ejb.repository.CustomerRepository;
import com.globaltrade.logistics.ejb.repository.RoleRepository;
import com.globaltrade.logistics.ejb.repository.UserRepository;
import com.globaltrade.logistics.ejb.security.PasswordService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.springframework.lang.NonNull;

import java.util.UUID;

@Stateless
@RolesAllowed({"ADMIN","CUSTOMER"})
public class CustomerServiceBean implements CustomerService {

    private static final String SEQUENCE_KEY = "CUSTOMER";
    private static final String PREFIX = "CUS";
    private static final int WIDTH = 3;

    @Inject
    private CustomerRepository customerRepository;
    @Inject
    private CompanyRepository companyRepository;
    @Inject
    private UserRepository userRepository;
    @Inject
    private PasswordService passwordService;
    @Inject
    private RoleRepository roleRepository;
    @Inject
    private NumberSequenceService numberSequenceService;

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public CustomerRegistrationResponse registerCustomer(@NonNull CustomerRegistrationRequest request) {

        Company company = companyRepository.findById(request.companyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new UsernameAlreadyExistsException(request.username());
        }

        Role customerRole = roleRepository.findByName(RoleType.CUSTOMER)
                .orElseThrow(() -> new ResourceNotFoundException("Customer role not configured"));

        String nextCustomerNumber = numberSequenceService.next(
                CustomerServiceBean.SEQUENCE_KEY,
                CustomerServiceBean.PREFIX,
                CustomerServiceBean.WIDTH
        );

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(passwordService.hashPassword(request.password()))
                .build();
        user.getRoles().add(customerRole);
        userRepository.save(user);

        //customer
        Customer customer = Customer.builder()
                .customerNumber(nextCustomerNumber)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .customerType(request.customerType())
                .email(request.email())
                .mobile1(request.mobile1())
                .mobile2(request.mobile2())
                .company(company)
                .user(user)
                .kycVerified(false)
                .build();

        customerRepository.save(customer);

        return new CustomerRegistrationResponse(
                customer.getId(),
                customer.getUser().getUsername(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getCompany().getId(),
                customer.getCustomerType(),
                customer.getStatus(),
                customer.isKycVerified()
        );
    }
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public UUID findCustomerIdByUsername(String username) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Customer customer = customerRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for user: " + username));

        return customer.getId();
    }
}
