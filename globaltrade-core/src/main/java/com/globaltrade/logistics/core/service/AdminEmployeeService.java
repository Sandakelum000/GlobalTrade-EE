package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.admin.employee.AdminEmployeeDetailsResponse;
import com.globaltrade.logistics.core.dto.admin.employee.AdminEmployeeListResponse;
import com.globaltrade.logistics.core.dto.admin.order.PageResponse;
import com.globaltrade.logistics.core.dto.employee.EmployeeRegistrationRequest;
import com.globaltrade.logistics.core.dto.employee.EmployeeRegistrationResponse;
import com.globaltrade.logistics.core.entity.employee.EmployeeDepartment;
import com.globaltrade.logistics.core.entity.employee.EmployeeStatus;
import jakarta.ejb.Local;

import java.util.UUID;

@Local
public interface AdminEmployeeService {
    EmployeeRegistrationResponse registerEmployee(EmployeeRegistrationRequest employeeRegistrationRequest);
    PageResponse<AdminEmployeeListResponse> getEmployees(
            String search,
            EmployeeDepartment department,
            EmployeeStatus status,
            String sortBy,
            String direction,
            int page,
            int size
    );

    AdminEmployeeDetailsResponse getEmployeeDetails(
            UUID employeeId
    );
}
