package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.admin.order.PageResponse;
import com.globaltrade.logistics.core.dto.admin.shipment.AdminShipmentDetailsResponse;
import com.globaltrade.logistics.core.dto.admin.shipment.AdminShipmentListResponse;
import com.globaltrade.logistics.core.entity.order.shipment.ShipmentStatus;
import jakarta.ejb.Local;

import java.util.UUID;

@Local
public interface AdminShipmentService{
    PageResponse<AdminShipmentListResponse> getShipments(
            String search,
            ShipmentStatus status,
            UUID warehouseId,
            String sortBy,
            String direction,
            int page,
            int size
    );

    AdminShipmentDetailsResponse getShipmentDetails(UUID shipmentId);
}
