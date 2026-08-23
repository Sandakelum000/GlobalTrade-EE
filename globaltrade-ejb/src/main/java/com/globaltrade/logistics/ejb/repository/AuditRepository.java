package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.audit.AuditLog;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class AuditRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public void save(AuditLog auditLog) {
        entityManager.persist(auditLog);
    }
}
