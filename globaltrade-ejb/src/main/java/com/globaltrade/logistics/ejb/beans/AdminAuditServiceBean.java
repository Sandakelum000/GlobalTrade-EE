package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.dto.admin.audit.AdminAuditDetailsResponse;
import com.globaltrade.logistics.core.dto.admin.audit.AdminAuditListResponse;
import com.globaltrade.logistics.core.dto.admin.order.PageResponse;
import com.globaltrade.logistics.core.entity.audit.AuditAction;
import com.globaltrade.logistics.core.entity.audit.AuditLog;
import com.globaltrade.logistics.core.exception.AuditServiceException;
import com.globaltrade.logistics.core.exception.ResourceNotFoundException;
import com.globaltrade.logistics.core.service.AdminAuditService;
import com.globaltrade.logistics.ejb.repository.AdminAuditRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Stateless
public class AdminAuditServiceBean implements AdminAuditService {
    private static final String SYSTEM = "SYSTEM";
    @Inject
    private AdminAuditRepository adminAuditRepository;

    @Override
    @RolesAllowed("ADMIN")
    @Transactional(Transactional.TxType.SUPPORTS)
    public PageResponse<AdminAuditListResponse> getAudits(String search, AuditAction action, UUID userId, LocalDateTime from, LocalDateTime to, String sortBy, String direction, int page, int size) {
        if (page < 0) {
            throw new AuditServiceException("Page cannot be negative");
        }
        if (size < 1 || size > 100) {
            throw new AuditServiceException("Size must be between 1 and 100");
        }

        if (from != null && to != null && from.isAfter(to)) {
            throw new AuditServiceException("From date cannot be after to date");
        }

        List<AuditLog> audits = adminAuditRepository.findAudits(search, action, userId, from, to,
                sortBy, direction, page, size);

        long totalElements = adminAuditRepository.countAudits(search, action, userId, from, to);


        List<AdminAuditListResponse> content = audits.stream()
                        .map(this::toListResponse)
                        .toList();

        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new PageResponse<>(
                content,
                page,
                size,
                totalElements,
                totalPages
        );
    }

    @Override
    @RolesAllowed("ADMIN")
    @Transactional(Transactional.TxType.SUPPORTS)
    public AdminAuditDetailsResponse getAuditDetails(UUID auditId) {
        if (auditId == null) {
            throw new AuditServiceException("Audit ID cannot be null");
        }

        AuditLog audit = adminAuditRepository.findByIdWithDetails(auditId)
                        .orElseThrow(() -> new ResourceNotFoundException("Audit log not found: " + auditId));


        return new AdminAuditDetailsResponse(
                audit.getId(),
                audit.getEntityType(),
                audit.getEntityId(),
                audit.getAction(),
                audit.getActionTimestamp(),

                audit.getUser() != null
                        ? audit.getUser().getId()
                        : null,

                audit.getUser() != null
                        ? audit.getUser().getUsername()
                        : "SYSTEM",

                audit.getDescription(),
                audit.getIpAddress()
        );
    }

    private AdminAuditListResponse toListResponse(AuditLog audit) {
        return new AdminAuditListResponse(
                audit.getId(),
                audit.getEntityType(),
                audit.getEntityId(),
                audit.getAction(),
                audit.getActionTimestamp(),

                audit.getUser() != null
                        ? audit.getUser().getId()
                        : null,

                audit.getUser() != null
                        ? audit.getUser().getUsername()
                        : SYSTEM,

                audit.getDescription()
        );
    }
}
