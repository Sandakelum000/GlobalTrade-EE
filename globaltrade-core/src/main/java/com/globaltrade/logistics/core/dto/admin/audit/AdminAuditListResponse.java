package com.globaltrade.logistics.core.dto.admin.audit;

import com.globaltrade.logistics.core.entity.audit.AuditAction;

import java.time.LocalDateTime;
import java.util.UUID;

public record AdminAuditListResponse(
        UUID auditId,

        String entityType,
        String entityId,

        AuditAction action,

        LocalDateTime actionTimestamp,

        UUID userId,
        String username,

        String description
) {
}
