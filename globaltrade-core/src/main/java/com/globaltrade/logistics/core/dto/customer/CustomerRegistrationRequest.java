package com.globaltrade.logistics.core.dto.customer;

import com.globaltrade.logistics.core.entity.customer.CustomerType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        String password,

        //company
        @NotNull(message = "Company is required")
        UUID companyId
) {
}
