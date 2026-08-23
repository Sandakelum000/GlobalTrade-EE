package com.globaltrade.logistics.core.entity.common;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class Address implements Serializable {
    @NotBlank
    @Size(max = 150)
    @Column(name = "address_line1", nullable = false, length = 150)
    private String line1;

    @Size(max = 150)
    @Column(name = "address_line2", length = 150)
    private String line2;

    @Size(max = 150)
    @Column(name = "address_line3", length = 150)
    private String line3;

    @NotBlank
    @Size(max = 80)
    @Column(name = "city", nullable = false, length = 80)
    private String city;

    @Size(max = 80)
    @Column(name = "district", length = 80)
    private String district;

    @Size(max = 80)
    @Column(name = "state_province", length = 80)
    private String stateProvince;

    @NotBlank
    @Size(max = 20)
    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;
}
