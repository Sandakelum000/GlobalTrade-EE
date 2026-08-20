package com.globaltrade.logistics.core.entity.warehouse;

import com.globaltrade.logistics.core.entity.common.BaseEntity;
import com.globaltrade.logistics.core.entity.grn.GRNItem;
import com.globaltrade.logistics.core.entity.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "inventory",
        indexes = {
                @Index(name = "idx_inventory_number", columnList = "inventory_number"),
                @Index(name = "idx_inventory_warehouse", columnList = "warehouse_id"),
                @Index(name = "idx_inventory_product", columnList = "product_id"),
                @Index(name = "idx_inventory_grn_item", columnList = "grn_item_id")
        }
)
@NamedQueries({
        @NamedQuery(name = "Inventory.findByWarehouseAndProduct",
                query = "SELECT i FROM Inventory i WHERE i.warehouse.id=:warehouseId AND i.product.id=:productId"),
        @NamedQuery(name = "Inventory.findById",query = "SELECT i FROM Inventory i WHERE i.id=:id")
})
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inventory extends BaseEntity {

    @NotBlank
    @Size(max = 30)
    @Column(name = "inventory_number", nullable = false, unique = true, length = 30)
    private String inventoryNumber;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "warehouse_id",
            nullable = false)
    private Warehouse warehouse;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "product_id",
            nullable = false
    )
    private Product product;

    @NotNull
    @Min(0)
    @Column(name = "quantity", nullable = false)
    @Builder.Default
    private Integer quantity = 0;

    @NotNull
    @Min(0)
    @Column(
            name = "reserved_quantity",
            nullable = false
    )
    @Builder.Default
    private Integer reservedQuantity = 0;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Selling price must be grater than 0")
    @Column(name = "selling_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal sellingPrice;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grn_item_id", nullable = false, foreignKey = @ForeignKey(name = "fk_inventory_grn_item"))
    private GRNItem grnItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private InventoryStatus status = InventoryStatus.ACTIVE;


    public int getAvailableQuantity() {
        return quantity - reservedQuantity;
    }

    public void addStockQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Stock amount should be greater than 0");
        }
        this.quantity += amount;
    }

    public void removeStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Stock amount should be greater than 0");
        }
        if (amount > getAvailableQuantity()) {
            throw new IllegalStateException("Insufficient stock amount");
        }
        this.quantity -= amount;
    }

    public void reserveStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Reservation amount must be greater than zero");
        }
        if (amount > getAvailableQuantity()) {
            throw new IllegalStateException("Insufficient available inventory");
        }
        this.reservedQuantity += amount;
    }

    public void releaseReservedStock(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Release amount must be greater than zero");
        }
        if (amount > reservedQuantity) {
            throw new IllegalStateException("Cannot release more than reserved quantity");
        }

        this.reservedQuantity -= amount;
    }
}
