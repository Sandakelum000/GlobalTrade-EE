package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.customs.CustomDocumentMonitorRecord;
import com.globaltrade.logistics.core.dto.customs.CustomsDocumentRegistrationRequest;
import com.globaltrade.logistics.core.dto.customs.CustomsDocumentResponse;
import jakarta.ejb.Local;

import java.util.List;
import java.util.UUID;

@Local
public interface CustomDocumentService {
    CustomsDocumentResponse createDocument(CustomsDocumentRegistrationRequest request);
    CustomsDocumentResponse findById(UUID id);
    List<CustomsDocumentResponse> findByShipment(UUID shipmentId);
    void deleteDocument(UUID documentId);
    List<CustomDocumentMonitorRecord> findShipmentWithMissingDocuments();
}
