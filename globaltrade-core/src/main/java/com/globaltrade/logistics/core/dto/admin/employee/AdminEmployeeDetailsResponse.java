package com.globaltrade.logistics.core.dto.admin.employee;

import com.globaltrade.logistics.core.entity.common.Address;
import com.globaltrade.logistics.core.entity.employee.EmployeeStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AdminEmployeeDetailsResponse(
        UUID employeeId,
        String employeeNumber,
        LocalDateTime joinedAt,

        String firstName,
        String lastName,
        String email,
        String mobile,

        List<String> roles,
        String department,

        Address address,

        UUID userId,
        String username,
        EmployeeStatus status
) {
}
