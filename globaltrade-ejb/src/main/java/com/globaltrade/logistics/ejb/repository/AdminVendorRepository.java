package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.company.Company;
import com.globaltrade.logistics.core.entity.grn.GRNStatus;
import com.globaltrade.logistics.core.entity.vendor.Vendor;
import com.globaltrade.logistics.core.entity.vendor.VendorStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.*;

@ApplicationScoped
public class AdminVendorRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Vendor> findVendors(String search,
                                    UUID companyId,
                                    VendorStatus status,
                                    String sortBy,
                                    String direction,
                                    int page,
                                    int size) {

        StringBuilder jpql = new StringBuilder("SELECT v FROM Vendor v JOIN FETCH v.company c WHERE 1 = 1 ");

        Map<String,Object> parameters = new HashMap<>();

        if(search != null && !search.isBlank()) {
            jpql.append(" AND (LOWER(v.vendorNumber) LIKE :search " +
                    "OR LOWER(v.contactFirstName) LIKE :search " +
                    "OR LOWER(v.contactLastName) LIKE :search " +
                    "OR LOWER(v.email) LIKE :search " +
                    "OR LOWER(c.name) LIKE :search )");

            parameters.put("search", "%" + search.trim().toLowerCase() + "%");
        }

        if (companyId != null) {
            jpql.append(" AND c.id = :companyId ");
            parameters.put("companyId", companyId);
        }

        if (status != null) {
            jpql.append(" AND v.status = :status ");
            parameters.put("status", status);
        }

        String sortField = switch (sortBy == null ? "vendorNumber" : sortBy) {
            case "companyName" -> "c.name";
            case "contactName" -> "v.contactFirstName";
            case "email" -> "v.email";
            case "performanceScore" -> "v.performanceScore";
            case "status" -> "v.status";
            default -> "v.vendorNumber";
        };

        String sortDirection = "ASC".equalsIgnoreCase(direction) ? "ASC" : "DESC";

        jpql.append(" ORDER BY ")
                .append(sortField)
                .append(" ")
                .append(sortDirection);


        TypedQuery<Vendor> query = entityManager.createQuery(jpql.toString(), Vendor.class);
        parameters.forEach(query::setParameter);

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    public long countVendors(String search, UUID companyId, VendorStatus status) {

        StringBuilder jpql = new StringBuilder("SELECT COUNT(v) FROM Vendor v JOIN v.company c WHERE 1 = 1 ");
        Map<String, Object> parameters = new HashMap<>();

        if (search != null && !search.isBlank()) {

            jpql.append("AND (LOWER(v.vendorNumber) LIKE :search OR LOWER(v.contactFirstName) " +
                    "LIKE :search OR LOWER(v.contactLastName) " +
                    "LIKE :search OR LOWER(v.email) LIKE :search OR LOWER(c.name) LIKE :search) ");

            parameters.put("search", "%" + search.trim().toLowerCase() + "%");
        }


        if (companyId != null) {
            jpql.append(" AND c.id = :companyId ");
            parameters.put("companyId", companyId);
        }

        if (status != null) {
            jpql.append(" AND v.status = :status ");
            parameters.put("status", status);
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql.toString(), Long.class);
        parameters.forEach(query::setParameter);

        return query.getSingleResult();
    }


    public Optional<Vendor> findByIdWithDetails(UUID id) {
        try {
            return Optional.of(entityManager.createQuery("select v from Vendor v JOIN FETCH v.company WHERE v.id = :id", Vendor.class)
                    .setParameter("id", id)
                    .getSingleResult());
        } catch (NoResultException nre) {
            return Optional.empty();
        }
    }

    public List<Company> findVendorCompanies() {
        return entityManager.createQuery("SELECT DISTINCT c FROM Company c " +
                        "JOIN Vendor v ON v.company.id = c.id ORDER BY c.name ASC", Company.class)
                .getResultList();
    }

    public Object[] getVendorGRNSummery(UUID vendorId){
        try{
            return entityManager.createQuery("SELECT COUNT(DISTINCT g),COALESCE(SUM(gi.unitCost * gi.quantity),0)," +
                    "MAX(g.receivedAt) FROM GoodsReceiveNote g LEFT JOIN g.items gi " +
                    "WHERE g.vendor.id=:vendorId AND g.status =:status",Object[].class)
                    .setParameter("vendorId",vendorId)
                    .setParameter("status", GRNStatus.RECEIVED)
                    .getSingleResult();
        }catch (NoResultException e){
            return new Object[]{};
        }
    }
}
