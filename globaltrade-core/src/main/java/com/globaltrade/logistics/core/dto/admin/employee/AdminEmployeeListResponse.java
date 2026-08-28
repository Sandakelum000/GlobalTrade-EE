package com.globaltrade.logistics.core.dto.admin.employee;

import com.globaltrade.logistics.core.entity.employee.EmployeeDepartment;
import com.globaltrade.logistics.core.entity.employee.EmployeeStatus;
import com.globaltrade.logistics.core.entity.security.RoleType;

import java.util.UUID;

public record AdminEmployeeListResponse(
        UUID employeeId,
        String employeeNumber,
        String firstName,
        String lastName,
        String email,
        String mobile,
        EmployeeDepartment roleType,
        EmployeeStatus status
) {
}
