package com.globaltrade.logistics.core.dto.employee;

import com.globaltrade.logistics.core.entity.employee.EmployeeDepartment;
import com.globaltrade.logistics.core.entity.employee.EmployeeStatus;

import java.util.UUID;

public record EmployeeRegistrationResponse(
        UUID employeeId,
        String employeeNumber,
        String username,
        String firstName,
        String lastName,
        String email,
        String mobile1,
        String mobile2,
        String departmentName,
        String country,
        String status
) {

}
