package com.globaltrade.logistics.core.dto.vendor;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record VendorRegistrationRequest(
        @NotBlank(message = "First Name is required")
        @Size(max = 45, message = "First Name must not be exceed 45 characters")
        String contactFirstName,

        @NotBlank(message = "Last Name is required")
        @Size(max = 45, message = "Last Name must not be exceed 45 characters")
        String contactLastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email address")
        @Size(max = 150, message = "Email must not be exceed 150 characters")
        String email,

        @NotBlank(message = "Please at least add one mobile number")
        @Size(max = 20,message = "mobile number must not be exceed 20 characters")
        String mobile1,

        @Size(max = 20,message = "mobile number must not be exceed 20 characters")
        String mobile2,

        @NotBlank(message = "Username is required for System access")
        @Size(min = 4, max = 30, message = "Username must be at least 4 character and not be exceed 30 characters")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be at least 8 characters and must not be exceed 100 characters")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must contain at least 8 characters, 1 letter, 1 number, and 1 special character"
        )
        String password,

        @NotNull(message = "Company is required")
        UUID companyId
) {
}
