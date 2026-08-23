package com.globaltrade.logistics.core.entity.common;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "country")
@NamedQueries({
        @NamedQuery(name = "Country.findByCountryId",query = "SELECT c FROM Country c WHERE c.id=:countryId"),
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Country extends BaseEntity {
    @NotBlank
    @Size(max = 150)
    @Column(name = "name", nullable = false, length = 150)
    private String name;
}
