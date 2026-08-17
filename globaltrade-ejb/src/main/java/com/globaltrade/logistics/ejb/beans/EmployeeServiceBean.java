package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.dto.employee.EmployeeRegistrationRequest;
import com.globaltrade.logistics.core.dto.employee.EmployeeRegistrationResponse;
import com.globaltrade.logistics.core.entity.common.Address;
import com.globaltrade.logistics.core.entity.employee.Employee;
import com.globaltrade.logistics.core.entity.security.Role;
import com.globaltrade.logistics.core.entity.security.User;
import com.globaltrade.logistics.core.exception.UsernameAlreadyExistsException;
import com.globaltrade.logistics.core.service.EmployeeService;
import com.globaltrade.logistics.core.service.NumberSequenceService;
import com.globaltrade.logistics.ejb.repository.EmployeeRepository;
import com.globaltrade.logistics.ejb.repository.RoleRepository;
import com.globaltrade.logistics.ejb.repository.UserRepository;
import com.globaltrade.logistics.ejb.security.PasswordService;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.springframework.lang.NonNull;

import java.util.Set;

@Stateless
public class EmployeeServiceBean implements EmployeeService {

    private static final String SEQUENCE_KEY = "EMPLOYEE";
    private static final String PREFIX = "EMP";
    private static final int WIDTH = 3;

    @Inject
    private EmployeeRepository employeeRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private RoleRepository roleRepository;

    @Inject
    private PasswordService passwordService;

    @Inject
    private NumberSequenceService numberSequenceService;


    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    public EmployeeRegistrationResponse registerEmployee(EmployeeRegistrationRequest request) {

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new UsernameAlreadyExistsException("Username already exists: " + request.username());
        }

        if (employeeRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + request.email());
        }

        Role employeeRole = roleRepository.findByName(request.roleType())
                .orElseThrow(() -> new IllegalArgumentException("Role type not found: "
                        + request.roleType()));


        String nextEmployeeNumber = numberSequenceService.next(
                EmployeeServiceBean.SEQUENCE_KEY,
                EmployeeServiceBean.PREFIX,
                EmployeeServiceBean.WIDTH
        );

        User user = User.builder()
                .username(request.username())
                .passwordHash(passwordService.hashPassword(request.password()))
                .email(request.email())
                .roles(Set.of(employeeRole))
                .build();
        userRepository.save(user);

        Address address = Address.builder()
                .country(request.country())
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

        employeeRepository.save(employee);
        return employeeRegResMapper(employee);

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
}
