package com.globaltrade.logistics.core.entity.grn;

import com.globaltrade.logistics.core.entity.common.BaseEntity;
import com.globaltrade.logistics.core.entity.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "grn_items",
        indexes = {
                @Index(name = "idx_grn_item_grn", columnList = "grn_id"),
                @Index(name = "idx_grn_item_product", columnList = "product_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GRNItem extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grn_id", nullable = false, foreignKey = @ForeignKey(name = "fk_grn_item_grn"))
    private GoodsReceiveNote grn;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_grn_item_product"))
    private Product product;

    @NotNull
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Unit cost must be greater than zero")
    @Column(name = "unit_cost", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitCost;

    public BigDecimal getTotalCost() {
        return unitCost.multiply(BigDecimal.valueOf(quantity));
    }
}
