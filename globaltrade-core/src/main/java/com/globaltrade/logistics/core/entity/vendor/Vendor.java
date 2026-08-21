package com.globaltrade.logistics.core.entity.vendor;

import com.globaltrade.logistics.core.entity.common.BaseEntity;
import com.globaltrade.logistics.core.entity.company.Company;
import com.globaltrade.logistics.core.entity.security.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(
        name = "vendors",
        indexes = {
                @Index(name = "idx_vendor_number", columnList = "vendor_number"),
                @Index(name = "idx_vendor_company_id", columnList = "company_id"),
                @Index(name = "idx_vendor_email", columnList = "email")
        }
)
@NamedQueries({
        @NamedQuery(name = "Vendor.findByVendorNumber",query = "SELECT v FROM Vendor v WHERE v.vendorNumber=:vendorNumber"),
        @NamedQuery(name = "Vendor.findActiveVendors",query = "SELECT v FROM Vendor v WHERE v.status=:status")
})
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vendor extends BaseEntity {
    @NotBlank
    @Size(max = 30)
    @Column(
            name = "vendor_number",
            nullable = false,
            unique = true,
            length = 30
    )
    private String vendorNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "company_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_vendor_company")
    )
    private Company company;

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
