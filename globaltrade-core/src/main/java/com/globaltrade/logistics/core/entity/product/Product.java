package com.globaltrade.logistics.core.entity.product;

import com.globaltrade.logistics.core.entity.common.BaseEntity;
import com.globaltrade.logistics.core.entity.vendor.Vendor;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "products",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_product_title", columnNames = "title")
        },
        indexes = {
                @Index(name = "idx_product_title", columnList = "title"),
                @Index(name = "idx_product_number", columnList = "product_number")
        }
)
@NamedQueries({
        @NamedQuery(name = "Product.findByProductNumber",query = "SELECT p FROM Product p WHERE p.productNumber=:productNumber"),
        @NamedQuery(name = "Product.existsByTitle",query = "SELECT COUNT(p) FROM Product p WHERE LOWER(p.title)=LOWER(:title) ")
})
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {
    @NotBlank
    @Size(max = 150)
    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @NotBlank
    @Size(max = 30)
    @Column(
            name = "product_number",
            nullable = false,
            unique = true,
            length = 30
    )
    private String productNumber;

    @Size(max = 500)
    @Column(name = "description", length = 500)
    private String description;

    @NotNull
    @Column(
            name = "reorder_level",
            nullable = false
    )
    @Builder.Default
    private Integer reorderLevel = 0;

}
