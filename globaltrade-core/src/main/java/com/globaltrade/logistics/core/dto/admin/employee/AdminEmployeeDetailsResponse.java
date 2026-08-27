package com.globaltrade.logistics.core.dto.admin.employee;

import com.globaltrade.logistics.core.entity.employee.EmployeeStatus;
import com.globaltrade.logistics.core.entity.security.RoleType;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminEmployeeDetailsResponse(
        UUID employeeId,
        String employeeNumber,
        LocalDateTime joinedAt,

        String firstName,
        String lastName,
        String email,
        String mobile,

        RoleType roleType,
        String department,

        String username,
        EmployeeStatus status
) {
}
