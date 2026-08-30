package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.entity.audit.AuditAction;
import com.globaltrade.logistics.core.entity.security.User;
import jakarta.ejb.Local;

@Local
public interface AuditLogService {
    void log(User user, AuditAction auditAction, String entityType, String entityId, String description);
    boolean existsByEntityAndAction(String entityType, String entityId, AuditAction action);
    void logLogin(String username);
    void logLogout(String username);
}
