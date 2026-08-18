package com.globaltrade.logistics.core.entity.grn;

import com.globaltrade.logistics.core.entity.common.BaseEntity;
import com.globaltrade.logistics.core.entity.vendor.Vendor;
import com.globaltrade.logistics.core.entity.warehouse.Warehouse;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "goods_received_notes",
        indexes = {
                @Index(name = "idx_grn_number", columnList = "grn_number", unique = true),
                @Index(name = "idx_grn_vendor", columnList = "vendor_id"),
                @Index(name = "idx_grn_warehouse", columnList = "warehouse_id"),
                @Index(name = "idx_grn_received_at", columnList = "received_at")
        }
)
@NamedQueries({
        @NamedQuery(name = "GRN.findByGrnNumber",query = "SELECT g FROM GoodsReceiveNote g WHERE g.grnNumber=:grnNumber")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsReceiveNote extends BaseEntity {

    @NotBlank
    @Column(name = "grn_number", nullable = false, unique = true, length = 30)
    private String grnNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendor_id", nullable = false, foreignKey = @ForeignKey(name = "fk_grn_vendor"))
    private Vendor vendor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false, foreignKey = @ForeignKey(name = "fk_grn_warehouse"))
    private Warehouse warehouse;

    @NotNull
    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private GRNStatus status = GRNStatus.DRAFT;

    @OneToMany(mappedBy = "grn", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GRNItem> items = new ArrayList<>();

    public void addItem(GRNItem item) {
        items.add(item);
        item.setGrn(this);
    }

    public void removeItem(GRNItem item) {
        items.remove(item);
        item.setGrn(null);
    }

    public void markReceived() {
        if (status != GRNStatus.DRAFT) {
            throw new IllegalStateException("Only draft GRNs can be marked as received");
        }

        if (items.isEmpty()) {
            throw new IllegalStateException("GRN must contain at least one item");
        }

        this.status = GRNStatus.RECEIVED;
        this.receivedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (status == GRNStatus.RECEIVED) {
            throw new IllegalStateException("A received GRN cannot be cancelled directly");
        }
        this.status = GRNStatus.CANCELLED;
    }

}
