package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.entity.audit.AuditAction;
import com.globaltrade.logistics.core.entity.audit.AuditLog;
import com.globaltrade.logistics.core.entity.security.User;
import com.globaltrade.logistics.core.service.AuditLogService;
import com.globaltrade.logistics.ejb.repository.AdminAuditRepository;
import com.globaltrade.logistics.ejb.repository.UserRepository;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;

import java.time.LocalDateTime;

@Stateless
public class AuditServiceBean implements AuditLogService {

    @Inject
    private AdminAuditRepository adminAuditRepository;
    @Inject
    private UserRepository userRepository;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void log(User user,AuditAction auditAction, String entityType, String entityId, String description) {
        AuditLog auditLog = AuditLog.builder()
                .user(user)
                .action(auditAction)
                .entityType(entityType)
                .entityId(entityId)
                .actionTimestamp(LocalDateTime.now())
                .description(description)
                .build();

        adminAuditRepository.save(auditLog);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public boolean existsByEntityAndAction(String entityType, String entityId, AuditAction action) {
        return adminAuditRepository.existsByEntityAndAction(entityType, entityId, action);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void logLogin(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        AuditLog auditLog = AuditLog.builder()
                .user(user)
                .action(AuditAction.LOGIN)
                .entityType("User")
                .entityId(user != null ? user.getId().toString() : username)
                .actionTimestamp(LocalDateTime.now())
                .description("User logged in successfully")
                .build();

        adminAuditRepository.save(auditLog);
    }

    @Override
    public void logLogout(String username) {
        User user = userRepository.findByUsername(username).orElse(null);

        AuditLog auditLog = AuditLog.builder()
                .user(user)
                .action(AuditAction.LOGOUT)
                .entityType("User")
                .entityId(user != null ? user.getId().toString() : username)
                .actionTimestamp(LocalDateTime.now())
                .description("User logged out")
                .build();

        adminAuditRepository.save(auditLog);
    }
}
