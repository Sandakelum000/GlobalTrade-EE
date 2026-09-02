package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.dto.admin.employee.AdminEmployeeDetailsResponse;
import com.globaltrade.logistics.core.dto.admin.employee.AdminEmployeeListResponse;
import com.globaltrade.logistics.core.dto.admin.order.PageResponse;
import com.globaltrade.logistics.core.dto.employee.EmployeeRegistrationRequest;
import com.globaltrade.logistics.core.dto.employee.EmployeeRegistrationResponse;
import com.globaltrade.logistics.core.entity.common.Address;
import com.globaltrade.logistics.core.entity.common.Country;
import com.globaltrade.logistics.core.entity.employee.Employee;
import com.globaltrade.logistics.core.entity.employee.EmployeeDepartment;
import com.globaltrade.logistics.core.entity.employee.EmployeeStatus;
import com.globaltrade.logistics.core.entity.security.Role;
import com.globaltrade.logistics.core.entity.security.User;
import com.globaltrade.logistics.core.exception.EmployeeServiceException;
import com.globaltrade.logistics.core.service.AdminEmployeeService;
import com.globaltrade.logistics.core.service.NumberSequenceService;
import com.globaltrade.logistics.ejb.repository.ContentRepository;
import com.globaltrade.logistics.ejb.repository.AdminEmployeeRepository;
import com.globaltrade.logistics.ejb.repository.RoleRepository;
import com.globaltrade.logistics.ejb.repository.UserRepository;
import com.globaltrade.logistics.ejb.security.PasswordService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Stateless
public class AdminEmployeeServiceBean implements AdminEmployeeService {

    private static final String SEQUENCE_KEY = "EMPLOYEE";
    private static final String PREFIX = "EMP";
    private static final int WIDTH = 3;

    @Inject
    private AdminEmployeeRepository adminEmployeeRepository;
    @Inject
    private UserRepository userRepository;
    @Inject
    private RoleRepository roleRepository;
    @Inject
    private PasswordService passwordService;
    @Inject
    private NumberSequenceService numberSequenceService;
    @Inject
    private ContentRepository contentRepository;


    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    @RolesAllowed({"ADMIN"})
    public EmployeeRegistrationResponse registerEmployee(EmployeeRegistrationRequest request) {

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new EmployeeServiceException("Username already exists: " + request.username());
        }

        if (adminEmployeeRepository.findByEmail(request.email()).isPresent()) {
            throw new EmployeeServiceException("Email already exists: " + request.email());
        }

        Country country = contentRepository.getCountryById(request.countryId())
                .orElseThrow(() -> new EmployeeServiceException("Country is not found: " + request.countryId()));

        Role employeeRole = roleRepository.findByName(request.roleType())
                .orElseThrow(() -> new EmployeeServiceException("Role type not found: "
                        + request.roleType()));


        String nextEmployeeNumber = numberSequenceService.next(
                AdminEmployeeServiceBean.SEQUENCE_KEY,
                AdminEmployeeServiceBean.PREFIX,
                AdminEmployeeServiceBean.WIDTH
        );

        User user = User.builder()
                .username(request.username())
                .passwordHash(passwordService.hashPassword(request.password()))
                .email(request.email())
                .roles(Set.of(employeeRole))
                .build();
        userRepository.save(user);

        Address address = Address.builder()
                .country(country)
                .district(request.district())
                .city(request.city())
                .line1(request.addressLine1())
                .line2(request.addressLine2())
                .line3(request.addressLine3())
                .stateProvince(request.stateProvince())
                .postalCode(request.postalCode())
                .build();

        Employee employee = Employee.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .employeeNumber(nextEmployeeNumber)
                .email(request.email())
                .department(request.department())
                .address(address)
                .mobile1(request.mobile1())
                .mobile2(request.mobile2())
                .user(user)
                .build();

        adminEmployeeRepository.save(employee);
        return employeeRegResMapper(employee);

    }

    @Override
    @RolesAllowed("ADMIN")
    @Transactional(Transactional.TxType.SUPPORTS)
    public PageResponse<AdminEmployeeListResponse> getEmployees(String search, EmployeeDepartment department, EmployeeStatus status, String sortBy, String direction, int page, int size) {
        if (page < 0) {
            throw new EmployeeServiceException("Page cannot be negative");
        }

        if (size < 1 || size > 100) {
            throw new EmployeeServiceException("Size must be between 1 and 100");
        }

        List<Employee> employees =
                adminEmployeeRepository.findEmployee(
                        search,
                        department,
                        status,
                        sortBy,
                        direction,
                        page,
                        size
                );

        long totalElements =
                adminEmployeeRepository.countEmployees(
                        search,
                        department,
                        status
                );

        List<AdminEmployeeListResponse> content =
                employees.stream()
                        .map(this::toListResponse)
                        .toList();

        int totalPages =
                (int) Math.ceil(
                        (double) totalElements / size
                );

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
    public AdminEmployeeDetailsResponse getEmployeeDetails(UUID employeeId) {
        if (employeeId == null) {
            throw new EmployeeServiceException("Employee ID cannot be null");
        }
        Employee employee = adminEmployeeRepository.findByIdWithDetails(employeeId)
                        .orElseThrow(() -> new EmployeeServiceException("Employee not found: " + employeeId));

        return toDetailsResponse(employee);
    }

    public EmployeeRegistrationResponse employeeRegResMapper(@NonNull Employee employee) {
        return new EmployeeRegistrationResponse(
                employee.getId(),
                employee.getEmployeeNumber(),
                employee.getUser().getUsername(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getMobile1(),
                employee.getMobile2(),
                employee.getDepartment().name(),
                employee.getAddress().getCountry(),
                employee.getStatus().name()
        );
    }

    private AdminEmployeeListResponse toListResponse(
            Employee employee) {

        return new AdminEmployeeListResponse(
                employee.getId(),
                employee.getEmployeeNumber(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getMobile1(),
                employee.getDepartment(),
                employee.getStatus()
        );
    }

    private AdminEmployeeDetailsResponse toDetailsResponse(
            Employee employee) {

        User user = employee.getUser();

        return new AdminEmployeeDetailsResponse(
                employee.getId(),
                employee.getEmployeeNumber(),
                employee.getCreatedAt(),

                employee.getFirstName(),
                employee.getLastName(),

                employee.getEmail(),
                employee.getMobile1(),
                employee.getUser().getRoles().stream().map(role -> role.getName().name()).toList(),

                employee.getDepartment().name(),
                employee.getAddress(),

                user.getId(),
                user.getUsername(),
                employee.getStatus()

        );
    }
}
