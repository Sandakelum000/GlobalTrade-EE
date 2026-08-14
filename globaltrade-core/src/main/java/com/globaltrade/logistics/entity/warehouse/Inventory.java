package com.globaltrade.logistics.entity.warehouse;

import com.globaltrade.logistics.entity.common.BaseEntity;
import com.globaltrade.logistics.entity.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(
        name = "inventory",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_inventory_warehouse_product",
                        columnNames = {
                                "warehouse_id",
                                "product_id"
                        }
                )
        },
        indexes = {
                @Index(name = "idx_inventory_warehouse", columnList = "warehouse_id"),
                @Index(name = "idx_inventory_product", columnList = "product_id")
        }
)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inventory extends BaseEntity {
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private InventoryStatus status = InventoryStatus.ACTIVE;

    public int getAvailableQuantity() {
        return quantity - reservedQuantity;
    }

    public void addStockQuantity(int amount) {
        if(amount <= 0){
            throw new IllegalArgumentException("Stock amount should be greater than 0");
        }
        this.quantity += quantity;
    }

    public void removeStock(int amount) {
        if(amount <= 0){
            throw new IllegalArgumentException("Stock amount should be greater than 0");
        }
        if(amount > getAvailableQuantity()){
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
