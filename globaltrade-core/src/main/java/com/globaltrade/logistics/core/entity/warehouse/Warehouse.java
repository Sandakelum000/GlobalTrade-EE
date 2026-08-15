package com.globaltrade.logistics.core.entity.warehouse;

import com.globaltrade.logistics.core.entity.common.Address;
import com.globaltrade.logistics.core.entity.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(
        name = "warehouses",
        indexes = {
                @Index(name = "idx_warehouse_name", columnList = "name"),
                @Index(name = "idx_warehouse_country", columnList = "country")
        }
)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Warehouse extends BaseEntity {
    @NotBlank
    @Size(max = 150)
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Valid
    @Embedded
    private Address address;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
