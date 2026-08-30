package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.admin.audit.AdminAuditDetailsResponse;
import com.globaltrade.logistics.core.dto.admin.audit.AdminAuditListResponse;
import com.globaltrade.logistics.core.dto.admin.order.PageResponse;
import com.globaltrade.logistics.core.entity.audit.AuditAction;
import jakarta.ejb.Local;

import java.time.LocalDateTime;
import java.util.UUID;

@Local
public interface AdminAuditService {
    PageResponse<AdminAuditListResponse> getAudits(
            String search,
            AuditAction action,
            UUID userId,
            LocalDateTime from,
            LocalDateTime to,
            String sortBy,
            String direction,
            int page,
            int size
    );

    AdminAuditDetailsResponse getAuditDetails(UUID auditId);
}
