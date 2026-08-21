package com.globaltrade.logistics.core.entity.order.shipment;

import com.globaltrade.logistics.core.entity.common.BaseEntity;
import com.globaltrade.logistics.core.entity.order.Order;
import com.globaltrade.logistics.core.entity.warehouse.Warehouse;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "shipments",
        indexes = {
                @Index(name = "idx_shipment_number", columnList = "shipment_number"),
                @Index(name = "idx_shipment_order", columnList = "order_id")
        }
)
@NamedQueries({
        @NamedQuery(name = "Shipment.findActiveShipments",query = "SELECT s FROM Shipment s WHERE s.status NOT IN (:delivered,:cancelled)")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment extends BaseEntity {

    @Column(name = "shipment_number",nullable = false, unique = true,length = 30)
    private String shipmentNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ShipmentStatus status;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "estimated_delivery_date")
    private LocalDateTime estimatedDeliveryDate;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
}
