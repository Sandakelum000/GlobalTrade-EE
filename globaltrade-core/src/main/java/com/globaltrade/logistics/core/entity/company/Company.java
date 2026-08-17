package com.globaltrade.logistics.core.entity.company;

import com.globaltrade.logistics.core.entity.common.Address;
import com.globaltrade.logistics.core.entity.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.*;

@Entity
@Table(
        name = "companies",
        indexes = {
                @Index(name = "idx_company_name", columnList = "name"),
                @Index(name = "idx_company_registration", columnList = "registration_number", unique = true)
        }
)
@NamedQueries({
        @NamedQuery(name = "Company.findByCompanyId",query = "SELECT c FROM Company c WHERE c.id=:companyId"),
        @NamedQuery(name = "Company.findByCompanyNumber",query = "SELECT c FROM Company c WHERE c.registrationNumber=:registrationNumber")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Company extends BaseEntity {

    @NotBlank
    @Size(max = 150)
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Size(max = 50)
    @Column(name = "registration_number", unique = true, length = 50)
    private String registrationNumber;

    @Email
    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "hotline_1", length = 20)
    private String hotline1;

    @Column(name = "hotline_2", length = 20)
    private String hotline2;

    @Embedded
    private Address address;
}