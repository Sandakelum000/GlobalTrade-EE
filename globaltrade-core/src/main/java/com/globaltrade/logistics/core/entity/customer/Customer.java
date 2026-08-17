package com.globaltrade.logistics.core.entity.customer;

import com.globaltrade.logistics.core.entity.common.Address;
import com.globaltrade.logistics.core.entity.common.BaseEntity;
import com.globaltrade.logistics.core.entity.company.Company;
import com.globaltrade.logistics.core.entity.security.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "customers",
        indexes = {
                @Index(name = "idx_customer_company_id", columnList = "company_id"),
                @Index(name = "idx_customer_email", columnList = "email")
        })
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends BaseEntity {

    @NotBlank
    @Size(max = 45)
    @Column(name = "first_name", nullable = false, length = 45)
    private String firstName;

    @NotBlank
    @Size(max = 45)
    @Column(name = "last_name", nullable = false, length = 45)
    private String lastName;

    @Email
    @Column(name = "email", length = 60)
    private String email;

    @Column(name = "mobile_1", length = 20)
    private String mobile1;

    @Column(name = "mobile_2", length = 20)
    private String mobile2;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            unique = true,
            foreignKey = @ForeignKey(name = "fk_customer_user")
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "company_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_customer_company")
    )
    private Company company;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", nullable = false)
    @Builder.Default
    private CustomerType customerType = CustomerType.LOCAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private CustomerStatus status = CustomerStatus.ACTIVE;

    @Column(name = "kyc_verified")
    @Builder.Default
    private boolean kycVerified = false;

    public void verifyKyc(){
        this.kycVerified = true;
    }
}
