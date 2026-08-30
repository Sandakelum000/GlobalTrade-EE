package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.audit.AuditAction;
import com.globaltrade.logistics.core.entity.audit.AuditLog;
import com.globaltrade.logistics.core.entity.security.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.*;

@ApplicationScoped
public class AdminAuditRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public void save(AuditLog auditLog) {
        entityManager.persist(auditLog);
    }

    public boolean existsByEntityAndAction(String entityType, String entityId, AuditAction action) {
        try{
            return entityManager.createQuery("SELECT COUNT(a) FROM AuditLog a WHERE " +
                            "a.entityType = :entityType AND a.entityId = :entityId AND a.action = :action", Long.class)
                    .setParameter("entityType", entityType)
                    .setParameter("entityId", entityId)
                    .setParameter("action", action)
                    .getSingleResult() > 0;
        }catch (NoResultException ex){
            return false;
        }

    }

    public Optional<AuditLog> findByIdWithDetails(UUID auditId) {
        List<AuditLog> result =
                entityManager.createQuery("SELECT a FROM AuditLog a LEFT JOIN FETCH a.user u " +
                                "WHERE a.id = :id", AuditLog.class)
                        .setParameter("id", auditId)
                        .setMaxResults(1)
                        .getResultList();

        return result.stream().findFirst();
    }


    public List<User> findAuditUsers() {
        return entityManager.createQuery("SELECT DISTINCT u FROM AuditLog a " +
                        "JOIN a.user u ORDER BY u.username ASC", User.class)
                .getResultList();
    }


    public List<AuditLog> findAudits(String search, AuditAction action, UUID userId, LocalDateTime from,
                                     LocalDateTime to, String sortBy, String direction, int page, int size) {

        StringBuilder jpql = new StringBuilder("""
                SELECT a
                FROM AuditLog a
                LEFT JOIN FETCH a.user u
                WHERE 1 = 1
                """);

        Map<String, Object> parameters = new HashMap<>();

        if (search != null && !search.isBlank()) {
            jpql.append("""
                    AND (
                        LOWER(a.entityType) LIKE :search
                        OR LOWER(a.entityId) LIKE :search
                        OR LOWER(a.description) LIKE :search
                        OR LOWER(u.username) LIKE :search
                    )
                    """);
            parameters.put("search", "%" + search.trim().toLowerCase() + "%");
        }

        if (action != null) {
            jpql.append(" AND a.action = :action ");
            parameters.put("action", action);
        }

        if (userId != null) {
            jpql.append(" AND u.id = :userId ");
            parameters.put("userId", userId);
        }

        if (from != null) {
            jpql.append(" AND a.actionTimestamp >= :from ");
            parameters.put("from", from);
        }

        if (to != null) {
            jpql.append(" AND a.actionTimestamp <= :to ");
            parameters.put("to", to);
        }

        String sortField = switch (sortBy == null ? "timestamp" : sortBy) {
            case "action" -> "a.action";
            case "entityType" -> "a.entityType";
            case "username" -> "u.username";
            default -> "a.actionTimestamp";
        };

        String sortDirection = "ASC".equalsIgnoreCase(direction) ? "ASC" : "DESC";
        jpql.append(" ORDER BY ").append(sortField).append(" ").append(sortDirection);

        TypedQuery<AuditLog> query = entityManager.createQuery(jpql.toString(), AuditLog.class);

        parameters.forEach(query::setParameter);

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }


    public long countAudits(
            String search,
            AuditAction action,
            UUID userId,
            LocalDateTime from,
            LocalDateTime to
    ) {

        StringBuilder jpql = new StringBuilder("""
                SELECT COUNT(a)
                FROM AuditLog a
                LEFT JOIN a.user u
                WHERE 1 = 1
                """);

        Map<String, Object> parameters = new HashMap<>();

        if (search != null && !search.isBlank()) {

            jpql.append("""
                    AND (
                        LOWER(a.entityType) LIKE :search
                        OR LOWER(a.entityId) LIKE :search
                        OR LOWER(a.description) LIKE :search
                        OR LOWER(u.username) LIKE :search
                    )
                    """);
            parameters.put("search", "%" + search.trim().toLowerCase() + "%");
        }

        if (action != null) {
            jpql.append(" AND a.action = :action ");
            parameters.put("action", action);
        }

        if (userId != null) {
            jpql.append(" AND u.id = :userId ");
            parameters.put("userId", userId);
        }

        if (from != null) {
            jpql.append(" AND a.actionTimestamp >= :from ");
            parameters.put("from", from);
        }

        if (to != null) {
            jpql.append(" AND a.actionTimestamp <= :to ");
            parameters.put("to", to);
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql.toString(), Long.class);
        parameters.forEach(query::setParameter);

        return query.getSingleResult();
    }
}
