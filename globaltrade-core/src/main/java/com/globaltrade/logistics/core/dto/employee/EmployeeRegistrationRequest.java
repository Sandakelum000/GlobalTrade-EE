package com.globaltrade.logistics.core.dto.employee;

import com.globaltrade.logistics.core.entity.employee.EmployeeDepartment;
import com.globaltrade.logistics.core.entity.security.RoleType;
import jakarta.validation.constraints.*;

public record EmployeeRegistrationRequest(
        @NotBlank(message = "First name is required")
        @Size(
                max = 45,
                message = "First name must not exceed 45 characters"
        )
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(
                max = 45,
                message = "Last name must not exceed 45 characters"
        )
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email address")
        @Size(
                max = 150,
                message = "Email must not exceed 150 characters"
        )
        String email,

        @Size(
                max = 20,
                message = "Mobile number must not exceed 20 characters"
        )
        String mobile1,

        @Size(
                max = 20,
                message = "Mobile number must not exceed 20 characters"
        )
        String mobile2,

        @NotNull(message = "Department is required")
        EmployeeDepartment department,

        @NotBlank(message = "Username is required")
        @Size(
                min = 4,
                max = 50,
                message = "Username must be between 4 and 50 characters"
        )
        String username,

        @NotBlank(message = "Password is required")
        @Size(
                min = 8,
                max = 100,
                message = "Password must be at least 8 characters and must not exceed 100 characters"
        )
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must contain at least 8 characters, 1 letter, 1 number, and 1 special character"
        )
        String password,

        @NotBlank(message = "Address line 1 is required")
        @Size(max = 150)
        String addressLine1,

        @Size(max = 150)
        String addressLine2,

        @Size(max = 150)
        String addressLine3,

        @NotBlank(message = "City is required")
        @Size(max = 80)
        String city,

        @Size(max = 80)
        String district,

        @Size(max = 80)
        String stateProvince,

        @NotBlank(message = "Postal code is required")
        @Size(max = 20)
        String postalCode,

        @NotBlank(message = "Country is required")
        @Size(max = 80)
        String country,

        @NotNull(message = "Role type is required")
        RoleType roleType
) {
}
