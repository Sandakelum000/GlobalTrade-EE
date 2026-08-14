package com.globaltrade.logistics.entity.employee;

import com.globaltrade.logistics.entity.common.Address;
import com.globaltrade.logistics.entity.common.BaseEntity;
import com.globaltrade.logistics.entity.security.User;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(
        name = "employees",
        indexes = {
                @Index(name = "idx_employee_number", columnList = "employee_number"),
                @Index(name = "idx_employee_email", columnList = "email")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends BaseEntity {
    @NotBlank
    @Size(max = 30)
    @Column(
            name = "employee_number",
            nullable = false,
            unique = true,
            length = 30
    )
    private String employeeNumber;

    @NotBlank
    @Size(max = 45)
    @Column(name = "first_name", nullable = false, length = 45)
    private String firstName;

    @NotBlank
    @Size(max = 45)
    @Column(name = "last_name", nullable = false, length = 45)
    private String lastName;

    @Email
    @NotBlank
    @Size(max = 150)
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Size(max = 20)
    @Column(name = "mobile_1", length = 20)
    private String mobile1;

    @Size(max = 20)
    @Column(name = "mobile_2", length = 20)
    private String mobile2;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private EmployeeStatus status = EmployeeStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "department", nullable = false, length = 50)
    private EmployeeDepartment department;

    @Valid
    @Embedded
    private Address address;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            unique = true,
            foreignKey = @ForeignKey(name = "fk_employee_user")
    )
    private User user;
}
