package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.entity.company.Company;
import com.globaltrade.logistics.core.entity.customer.Customer;
import com.globaltrade.logistics.core.entity.customer.CustomerStatus;
import com.globaltrade.logistics.core.entity.customer.CustomerType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@ApplicationScoped
public class AdminCustomerRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Customer> findCustomers(
            String search,
            UUID companyId,
            CustomerType customerType,
            CustomerStatus status,
            Boolean kycVerified,
            String sortBy,
            String direction,
            int page,
            int size
    ) {

        StringBuilder jpql = new StringBuilder("SELECT c FROM Customer c JOIN FETCH c.company company " +
                "WHERE 1 = 1 ");

        Map<String, Object> parameters = new HashMap<>();

        if (search != null && !search.isBlank()) {

            jpql.append("""
                AND (
                    LOWER(c.customerNumber) LIKE :search
                    OR LOWER(c.firstName) LIKE :search
                    OR LOWER(c.lastName) LIKE :search
                    OR LOWER(c.email) LIKE :search
                    OR LOWER(company.name) LIKE :search
                )
                """);
            parameters.put("search", "%" + search.trim().toLowerCase() + "%");
        }

        if (companyId != null) {
            jpql.append(" AND company.id = :companyId ");
            parameters.put("companyId", companyId);
        }

        if (customerType != null) {
            jpql.append(" AND c.customerType = :customerType ");
            parameters.put("customerType", customerType);
        }

        if (status != null) {
            jpql.append(" AND c.status = :status ");
            parameters.put("status", status);
        }

        if (kycVerified != null) {
            jpql.append(" AND c.kycVerified = :kycVerified ");
            parameters.put("kycVerified", kycVerified);
        }

        String sortField = switch (sortBy == null ? "customerNumber" : sortBy) {
            case "customerName" -> "c.firstName";
            case "companyName" -> "company.name";
            case "email" -> "c.email";
            case "customerType" -> "c.customerType";
            case "status" -> "c.status";
            case "kycVerified" -> "c.kycVerified";
            case "joinedAt" -> "c.createdAt";

            default -> "c.customerNumber";
        };

        String sortDirection = "ASC".equalsIgnoreCase(direction) ? "ASC" : "DESC";

        jpql.append(" ORDER BY ")
                .append(sortField)
                .append(" ")
                .append(sortDirection);

        TypedQuery<Customer> query = entityManager.createQuery(jpql.toString(), Customer.class);
        parameters.forEach(query::setParameter);

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }


    public List<Company> findCustomerCompanies() {
        return entityManager.createQuery("SELECT DISTINCT company FROM Customer c " +
                        "JOIN c.company company ORDER BY company.name ASC", Company.class)
                .getResultList();
    }

    public Optional<Customer> findByIdWithDetails(UUID customerId) {
        try {
            Customer customer = entityManager.createQuery("SELECT c FROM Customer c " +
                            "JOIN FETCH c.company company WHERE c.id = :id", Customer.class)
                    .setParameter("id", customerId)
                    .getSingleResult();
            return Optional.of(customer);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public long countOrdersByCustomer(UUID customerId) {

        return entityManager.createQuery("SELECT COUNT(o) FROM Order o WHERE o.customer.id = :customerId", Long.class)
                .setParameter("customerId", customerId)
                .getSingleResult();
    }

    public BigDecimal calculateTotalOrderValue(UUID customerId) {

        return entityManager.createQuery("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
                        "WHERE o.customer.id = :customerId", BigDecimal.class)
                .setParameter("customerId", customerId)
                .getSingleResult();
    }

    public Optional<LocalDateTime> findLastOrderDate(UUID customerId) {

        List<LocalDateTime> result = entityManager.createQuery("SELECT o.createdAt FROM Order o WHERE o.customer.id = :customerId " +
                        "ORDER BY o.createdAt DESC", LocalDateTime.class)
                .setParameter("customerId", customerId)
                .setMaxResults(1)
                .getResultList();

        return result.stream().findFirst();
    }

    public long countCustomers(String search, UUID companyId, CustomerType customerType, CustomerStatus status, Boolean kycVerified) {

        StringBuilder jpql = new StringBuilder("SELECT COUNT(c) FROM Customer c " +
                "JOIN c.company company WHERE 1 = 1 ");

        Map<String, Object> params = new HashMap<>();

        if (search != null && !search.isBlank()) {
            jpql.append("""
                AND (
                    LOWER(c.customerNumber) LIKE :search
                    OR LOWER(c.firstName) LIKE :search
                    OR LOWER(c.lastName) LIKE :search
                    OR LOWER(c.email) LIKE :search
                    OR LOWER(company.name) LIKE :search
                )
                """);

            params.put(
                    "search",
                    "%" + search.trim().toLowerCase() + "%"
            );
        }

        if (companyId != null) {
            jpql.append(" AND company.id = :companyId ");
            params.put("companyId", companyId);
        }

        if (customerType != null) {
            jpql.append(" AND c.customerType = :customerType ");
            params.put("customerType", customerType);
        }

        if (status != null) {
            jpql.append(" AND c.status = :status ");
            params.put("status", status);
        }

        if (kycVerified != null) {
            jpql.append(" AND c.kycVerified = :kycVerified ");
            params.put("kycVerified", kycVerified);
        }

        TypedQuery<Long> query =
                entityManager.createQuery(
                        jpql.toString(),
                        Long.class
                );

        params.forEach(query::setParameter);

        return query.getSingleResult();
    }
}
