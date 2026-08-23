package com.globaltrade.logistics.core.entity.order.shipment;

import com.globaltrade.logistics.core.entity.common.Address;
import com.globaltrade.logistics.core.entity.common.BaseEntity;
import com.globaltrade.logistics.core.entity.order.Order;
import com.globaltrade.logistics.core.entity.warehouse.Warehouse;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ShipmentItem> items = new ArrayList<>();

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "estimated_delivery_date")
    private LocalDateTime estimatedDeliveryDate;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "priority", nullable = false)
    @Builder.Default
    private Integer priority = 0;

    @Column(name = "route_name", length = 150)
    private String routeName;

    @Column(name = "route_distance_km", precision = 10, scale = 2)
    private BigDecimal routeDistanceKm;

    @Column(name = "route_estimated_hours", precision = 10, scale = 2)
    private BigDecimal routeEstimatedHours;

    @Column(name = "route_risk_score")
    private Integer routeRiskScore;

    //address
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "line1",
                    column = @Column(name = "origin_address_line1")),
            @AttributeOverride(name = "line2",
                    column = @Column(name = "origin_address_line2")),
            @AttributeOverride(name = "line3",
                    column = @Column(name = "origin_address_line3")),
            @AttributeOverride(name = "city",
                    column = @Column(name = "origin_city")),
            @AttributeOverride(name = "district",
                    column = @Column(name = "origin_district")),
            @AttributeOverride(name = "stateProvince",
                    column = @Column(name = "origin_state_province")),
            @AttributeOverride(name = "postalCode",
                    column = @Column(name = "origin_postal_code"))
    })
    @AssociationOverride(
            name = "country",
            joinColumns = @JoinColumn(
                    name = "origin_country_id",
                    nullable = false
            )
    )
    private Address originAddress;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "line1",
                    column = @Column(name = "destination_address_line1")),
            @AttributeOverride(name = "line2",
                    column = @Column(name = "destination_address_line2")),
            @AttributeOverride(name = "line3",
                    column = @Column(name = "destination_address_line3")),
            @AttributeOverride(name = "city",
                    column = @Column(name = "destination_city")),
            @AttributeOverride(name = "district",
                    column = @Column(name = "destination_district")),
            @AttributeOverride(name = "stateProvince",
                    column = @Column(name = "destination_state_province")),
            @AttributeOverride(name = "postalCode",
                    column = @Column(name = "destination_postal_code")),
    })
    @AssociationOverride(
            name = "country",
            joinColumns = @JoinColumn(
                    name = "destination_country_id",
                    nullable = false
            )
    )
    private Address destinationAddress;

    public void addItem(ShipmentItem item) {
        items.add(item);
        item.setShipment(this);
    }
}
