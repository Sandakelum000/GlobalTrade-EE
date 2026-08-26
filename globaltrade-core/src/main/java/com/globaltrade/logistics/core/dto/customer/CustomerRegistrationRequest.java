package com.globaltrade.logistics.core.dto.customer;

import com.globaltrade.logistics.core.entity.customer.CustomerType;
import jakarta.validation.constraints.*;

import java.util.UUID;

public record CustomerRegistrationRequest(
        @NotBlank(message = "First name is required")
        @Size(max = 45, message = "First name must not exceed 45 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 45, message = "Last name must not exceed 45 characters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email address")
        @Size(max = 60, message = "Email must not exceed 60 characters")
        String email,

        @NotBlank(message = "Mobile number is required")
        @Size(max = 20, message = "Mobile number must not exceed 20 characters")
        String mobile1,

        @Size(max = 20, message = "Mobile number must not exceed 20 characters")
        String mobile2,

        @NotNull(message = "Customer type is required")
        CustomerType customerType,

        @NotBlank(message = "Username is required")
        @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be at least 8 characters and must not be exceed 100 characters")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must contain at least 8 characters, 1 letter, 1 number, and 1 special character"
        )
        String password,

        //company
        @NotNull(message = "Company is required")
        UUID companyId
) {
}
