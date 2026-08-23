package com.globaltrade.logistics.core.entity.order.shipment;

import com.globaltrade.logistics.core.entity.common.BaseEntity;
import com.globaltrade.logistics.core.entity.order.OrderItem;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "shipment_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_shipment_order_item",
                        columnNames = {"shipment_id", "order_item_id"}
                )
        },
        indexes = {
                @Index(name = "idx_shipment_item_shipment", columnList = "shipment_id"),
                @Index(name = "idx_shipment_item_order_item", columnList = "order_item_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentItem extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "shipment_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_shipment_item_shipment")
    )
    private Shipment shipment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "order_item_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_shipment_item_order_item")
    )
    private OrderItem orderItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private ShipmentItemStatus status = ShipmentItemStatus.PENDING;
}
