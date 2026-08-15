package com.globaltrade.logistics.core.entity.vendor;

import com.globaltrade.logistics.core.entity.common.Address;
import com.globaltrade.logistics.core.entity.common.BaseEntity;
import com.globaltrade.logistics.core.entity.security.User;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(
        name = "vendors",
        indexes = {
                @Index(
                        name = "idx_vendor_company_name",
                        columnList = "company_name"
                ),
                @Index(
                        name = "idx_vendor_email",
                        columnList = "email"
                )
        }
)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vendor extends BaseEntity {

    @NotBlank
    @Size(max = 150)
    @Column(
            name = "company_name",
            nullable = false,
            length = 150
    )
    private String companyName;

    @NotBlank
    @Size(max = 45)
    @Column(name = "contact_first_name", nullable = false, length = 45)
    private String contactFirstName;

    @NotBlank
    @Size(max = 45)
    @Column(name = "contact_last_name", nullable = false, length = 45)
    private String contactLastName;

    @Email
    @Size(max = 150)
    @Column(name = "email", length = 150)
    private String email;

    @Size(max = 20)
    @Column(name = "mobile_1", length = 20)
    private String mobile1;

    @Size(max = 20)
    @Column(name = "mobile_2", length = 20)
    private String mobile2;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            unique = true,
            foreignKey = @ForeignKey(name = "fk_vendor_user")
    )
    private User user;

    @Valid
    @Embedded
    private Address address;

    @Column(
            name = "performance_score",
            nullable = false
    )
    @Builder.Default
    private Integer performanceScore = 0;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    @Builder.Default
    private VendorStatus status = VendorStatus.ACTIVE;

}
