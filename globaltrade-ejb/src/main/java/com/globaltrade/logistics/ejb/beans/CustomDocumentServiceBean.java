package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.dto.customs.CustomDocumentMonitorRecord;
import com.globaltrade.logistics.core.dto.customs.CustomsDocumentRegistrationRequest;
import com.globaltrade.logistics.core.dto.customs.CustomsDocumentResponse;
import com.globaltrade.logistics.core.entity.order.shipment.CustomsDocument;
import com.globaltrade.logistics.core.entity.order.shipment.CustomsDocumentType;
import com.globaltrade.logistics.core.entity.order.shipment.Shipment;
import com.globaltrade.logistics.core.entity.order.shipment.ShipmentStatus;
import com.globaltrade.logistics.core.service.CustomDocumentService;
import com.globaltrade.logistics.ejb.repository.CustomDocumentRepository;
import com.globaltrade.logistics.ejb.repository.ShipmentRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Stateless
public class CustomDocumentServiceBean implements CustomDocumentService {

    private static final List<CustomsDocumentType> REQUIRED_DOCUMENTS = List.of(
      CustomsDocumentType.COMMERCIAL_INVOICE,
      CustomsDocumentType.PACKING_LIST,
      CustomsDocumentType.CERTIFICATE_OF_ORIGIN,
      CustomsDocumentType.CUSTOMS_DECLARATION
    );

    @Inject
    private CustomDocumentRepository customDocumentRepository;
    @Inject
    private ShipmentRepository shipmentRepository;

    @Override
    @RolesAllowed({"ADMIN"})
    @Transactional(Transactional.TxType.REQUIRED)
    public CustomsDocumentResponse createDocument(CustomsDocumentRegistrationRequest request) {
        Shipment shipment = shipmentRepository.findById(request.shipmentId())
                .orElseThrow(() -> new IllegalArgumentException("shipment id not found: " + request.shipmentId()));


        if (shipment.getStatus() == ShipmentStatus.CANCELLED) {
            throw new IllegalStateException("This shipment is already cancelled");
        }

        CustomsDocument customsDocument = CustomsDocument.builder()
                .shipment(shipment)
                .documentType(request.documentType())
                .documentNumber(request.documentNumber())
                .fileName(request.fileName())
                .filePath(request.filePath())
                .issuedAt(LocalDateTime.now())
                .build();

        customDocumentRepository.save(customsDocument);
        return toResponse(customsDocument);
    }

    @Override
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    @Transactional(Transactional.TxType.SUPPORTS)
    public CustomsDocumentResponse findById(UUID id) {
        CustomsDocument document = customDocumentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customs document not found: " + id));
        return toResponse(document);
    }

    @Override
    @RolesAllowed({"ADMIN", "CUSTOMER"})
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<CustomsDocumentResponse> findByShipment(UUID shipmentId) {
        return customDocumentRepository.findByShipmentIdDESC(shipmentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @RolesAllowed({"ADMIN"})
    @Transactional(Transactional.TxType.REQUIRED)
    public void deleteDocument(UUID documentId) {
        CustomsDocument document = customDocumentRepository.findById(documentId)
                        .orElseThrow(() -> new IllegalArgumentException("Customs document not found: " + documentId));

        customDocumentRepository.delete(document);
    }

    //timer eke method eka
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<CustomDocumentMonitorRecord> findShipmentWithMissingDocuments() {
        return shipmentRepository.findActiveShipments()
                .stream()
                .map(shipment -> {
                    List<CustomsDocumentType> existingDocuments = customDocumentRepository
                            .findByShipmentId(shipment.getId())
                            .stream()
                            .map(CustomsDocument::getDocumentType)
                            .toList();

                    List<CustomsDocumentType> missingDocuments = REQUIRED_DOCUMENTS.stream()
                            .filter(type -> !existingDocuments.contains(type))
                            .toList();

                    return new CustomDocumentMonitorRecord(
                            shipment.getId(),
                            shipment.getShipmentNumber(),
                            shipment.getStatus(),
                            missingDocuments
                    );
                }).filter(record-> !record.missingDocuments().isEmpty())
                .toList();
    }

    private CustomsDocumentResponse toResponse(CustomsDocument document) {
        return new CustomsDocumentResponse(
                document.getId(),
                document.getDocumentNumber(),
                document.getDocumentType(),
                document.getFileName(),
                document.getFilePath(),
                document.getShipment().getShipmentNumber(),
                document.getIssuedAt()
        );
    }
}
