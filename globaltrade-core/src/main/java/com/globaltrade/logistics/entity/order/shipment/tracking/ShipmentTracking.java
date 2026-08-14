package com.globaltrade.logistics.entity.order.shipment.tracking;

import com.globaltrade.logistics.entity.common.BaseEntity;
import com.globaltrade.logistics.entity.order.shipment.Shipment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "shipment_tracking",
        indexes = {
                @Index(name = "idx_tracking_shipment", columnList = "shipment_id"),
                @Index(name = "idx_tracking_timestamp", columnList = "tracking_timestamp")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentTracking extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false,length = 30)
    private TrackingStatus status;

    @Column(name = "description", length = 300)
    private String description;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "tracking_timestamp", nullable = false)
    private LocalDateTime trackingTimestamp;
}
