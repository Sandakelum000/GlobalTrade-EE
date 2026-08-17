package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.employee.EmployeeRegistrationRequest;
import com.globaltrade.logistics.core.dto.employee.EmployeeRegistrationResponse;
import jakarta.ejb.Local;

@Local
public interface EmployeeService {
    EmployeeRegistrationResponse registerEmployee(EmployeeRegistrationRequest employeeRegistrationRequest);
}
