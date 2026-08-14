package com.globaltrade.logistics.entity.order.shipment;

import com.globaltrade.logistics.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "customs_documents",
        indexes = {
                @Index(name = "idx_customs_document_shipment", columnList = "shipment_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomsDocument extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", length = 20,nullable = false)
    private CustomsDocumentType documentType;

    @Column(name = "document_number", length = 40, unique = true)
    private String documentNumber;

    @Column(name = "file_name",nullable = false)
    private String fileName;

    @Column(name = "file_path",nullable = false,length = 500)
    private String filePath;

    @Column(name = "issued_at")
    private Instant issuedAt;
}
