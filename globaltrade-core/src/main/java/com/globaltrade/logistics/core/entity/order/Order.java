package com.globaltrade.logistics.core.entity.order;

import com.globaltrade.logistics.core.entity.common.Address;
import com.globaltrade.logistics.core.entity.common.BaseEntity;
import com.globaltrade.logistics.core.entity.customer.Customer;
import com.globaltrade.logistics.core.entity.order.shipment.Shipment;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_order_number", columnList = "order_number"),
                @Index(name = "idx_order_customer", columnList = "customer_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Column(name = "order_number",nullable = false,unique = true,length = 40)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "customer_id",nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false,length = 20)
    private OrderStatus orderStatus;

    @Column(name = "order_date",nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Shipment> shipments = new ArrayList<>();

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }

    //address
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "line1",
                    column = @Column(name = "shipping_address_line1")),
            @AttributeOverride(name = "line2",
                    column = @Column(name = "shipping_address_line2")),
            @AttributeOverride(name = "line3",
                    column = @Column(name = "shipping_address_line3")),
            @AttributeOverride(name = "city",
                    column = @Column(name = "shipping_city")),
            @AttributeOverride(name = "district",
                    column = @Column(name = "shipping_district")),
            @AttributeOverride(name = "stateProvince",
                    column = @Column(name = "shipping_state_province")),
            @AttributeOverride(name = "postalCode",
                    column = @Column(name = "shipping_postal_code")),
    })
    @AssociationOverride(
            name = "country",
            joinColumns = @JoinColumn(
                    name = "shipping_country_id",
                    nullable = false
            )
    )
    private Address shippingAddress;
}
