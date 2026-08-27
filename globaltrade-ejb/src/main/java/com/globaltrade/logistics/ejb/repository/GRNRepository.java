package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.company.Company;
import com.globaltrade.logistics.core.entity.grn.GRNStatus;
import com.globaltrade.logistics.core.entity.grn.GoodsReceiveNote;
import com.globaltrade.logistics.core.entity.vendor.Vendor;
import com.globaltrade.logistics.core.entity.warehouse.Warehouse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.*;

@ApplicationScoped
public class GRNRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(GoodsReceiveNote grn) {
        entityManager.persist(grn);
    }

    public Optional<GoodsReceiveNote> findByGrnId(UUID grnId) {
        try {
            return Optional.of(entityManager.find(GoodsReceiveNote.class, grnId));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }


    public long countByVendor(UUID vendorId) {
        try {
            return entityManager.createQuery("SELECT COUNT(g) FROM GoodsReceiveNote g WHERE g.vendor.id=:vendorId", Long.class)
                    .setParameter("vendorId", vendorId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0;
        }
    }

    public long successfulCountByVendor(UUID vendorId) {
        try {
            return entityManager.createQuery("SELECT COUNT(g) FROM GoodsReceiveNote g" +
                            " WHERE g.vendor.id=:vendorId " + "AND g.status=:status", Long.class)
                    .setParameter("vendorId", vendorId)
                    .setParameter("status", GRNStatus.RECEIVED)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0;
        }
    }

    //admin
    public List<GoodsReceiveNote> findGRNs(String search, UUID companyId, UUID warehouseId, GRNStatus status,
                                           String sortBy, String direction, int page, int size) {

        StringBuilder jpql = new StringBuilder("SELECT DISTINCT g FROM GoodsReceiveNote g JOIN FETCH g.vendor v JOIN FETCH v.company c " +
                "JOIN FETCH g.warehouse w WHERE 1 = 1 ");

        Map<String, Object> parameters = new HashMap<>();

        if (search != null && !search.isBlank()) {
            jpql.append("AND (LOWER(g.grnNumber) LIKE :search OR LOWER(v.contactFirstName) LIKE :search OR LOWER(v.contactLastName) LIKE :search OR LOWER(w.name) LIKE :search) ");
            parameters.put("search", "%" + search.trim().toLowerCase() + "%");
        }

        if (companyId != null) {
            jpql.append(" AND c.id = :companyId ");
            parameters.put("companyId", companyId);
        }

        if (warehouseId != null) {
            jpql.append(" AND w.id = :warehouseId ");
            parameters.put("warehouseId", warehouseId);
        }

        if (status != null) {
            jpql.append(" AND g.status = :status ");
            parameters.put("status", status);
        }

        String sortField = switch (sortBy == null ? "receivedAt" : sortBy) {
            case "grnNumber" -> "g.grnNumber";
            case "vendorName" -> "v.contactFirstName";
            case "warehouseName" -> "w.name";
            case "totalCost" -> "g.totalCost";
            case "status" -> "g.status";
            default -> "g.receivedAt";
        };

        String sortDirection = "ASC".equalsIgnoreCase(direction) ? "ASC" : "DESC";

        jpql.append(" ORDER BY ")
                .append(sortField)
                .append(" ")
                .append(sortDirection);

        TypedQuery<GoodsReceiveNote> query = entityManager.createQuery(jpql.toString(), GoodsReceiveNote.class);

        parameters.forEach(query::setParameter);

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }


    public long countGRNs(String search, UUID companyId, UUID warehouseId, GRNStatus status) {

        StringBuilder jpql = new StringBuilder("SELECT COUNT(g) FROM GoodsReceiveNote g " +
                "JOIN g.vendor v JOIN v.company c JOIN g.warehouse w WHERE 1 = 1 ");

        Map<String, Object> parameters = new HashMap<>();

        if (search != null && !search.isBlank()) {
            jpql.append("AND (LOWER(g.grnNumber) LIKE :search OR LOWER(v.contactFirstName) LIKE :search OR LOWER(v.contactLastName) LIKE :search OR LOWER(w.name) LIKE :search) ");
            parameters.put("search", "%" + search.trim().toLowerCase() + "%");
        }

        if (companyId != null) {
            jpql.append(" AND c.id = :companyId ");
            parameters.put("companyId", companyId);
        }

        if (warehouseId != null) {
            jpql.append(" AND w.id = :warehouseId ");
            parameters.put("warehouseId", warehouseId);
        }

        if (status != null) {
            jpql.append(" AND g.status = :status ");
            parameters.put("status", status);
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql.toString(), Long.class);

        parameters.forEach(query::setParameter);

        return query.getSingleResult();
    }

    public Optional<GoodsReceiveNote> findDetailsById(UUID grnId) {

        List<GoodsReceiveNote> results =
                entityManager.createQuery("SELECT DISTINCT g FROM GoodsReceiveNote g " +
                                "JOIN FETCH g.vendor JOIN FETCH g.warehouse LEFT JOIN FETCH g.items gi " +
                                "LEFT JOIN FETCH gi.product WHERE g.id = :grnId", GoodsReceiveNote.class)
                        .setParameter("grnId", grnId)
                        .setMaxResults(1)
                        .getResultList();

        return results.stream().findFirst();
    }

    public List<Company> findVendorCompanies() {
        return entityManager.createQuery("SELECT DISTINCT v.company FROM Vendor v ORDER BY v.company.name ASC", Company.class)
                .getResultList();
    }
}


