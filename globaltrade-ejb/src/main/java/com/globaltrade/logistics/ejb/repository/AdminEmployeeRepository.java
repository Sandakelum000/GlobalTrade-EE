package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.employee.Employee;
import com.globaltrade.logistics.core.entity.employee.EmployeeDepartment;
import com.globaltrade.logistics.core.entity.employee.EmployeeStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.*;

@ApplicationScoped
public class AdminEmployeeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(Employee employee) {
        entityManager.persist(employee);
    }

    public Optional<Employee> findEmployeeByNumber(String employeeNumber) {
        try {
            return Optional.of(entityManager.createNamedQuery("Employee.findByNumber", Employee.class)
                    .setParameter("employeeNumber", employeeNumber)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Employee> findById(UUID employeeId) {
        return Optional.ofNullable(entityManager.find(Employee.class, employeeId));
    }

    public Optional<Employee> findByEmail(String email) {
        try {
            return Optional.of(entityManager.createNamedQuery("Employee.findByEmail", Employee.class)
                    .setParameter("email", email)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }


    public List<Employee> findEmployee(String search, EmployeeDepartment department,
                                       EmployeeStatus status, String sortBy, String direction, int page, int size) {

        StringBuilder jpql = new StringBuilder("SELECT e FROM Employee e LEFT JOIN FETCH e.user u WHERE 1=1 ");

        Map<String, Object> parameters = new HashMap<>();

        if (search != null && !search.isBlank()) {
            jpql.append(" AND (LOWER(e.employeeNumber) LIKE :search OR LOWER(e.firstName) LIKE :search OR LOWER(e.lastName) LIKE :search " +
                    "OR LOWER(e.email) LIKE :search OR LOWER(u.username) LIKE :search ) ");
            parameters.put("search", "%" + search.trim().toLowerCase() + "%");
        }

        if (department != null) {
            jpql.append(" AND e.department = :department ");
            parameters.put("department", department);
        }

        if (status != null) {
            jpql.append(" AND e.status = :status ");
            parameters.put("status", status);
        }

        String sortField = switch (sortBy == null ? "employeeNumber" : sortBy) {
            case "firstName" -> "e.firstName";
            case "lastName" -> "e.lastName";
            case "email" -> "e.email";
            case "department" -> "e.department";
            case "status" -> "e.status";
            case "joinedAt" -> "e.createdAt";
            default -> "e.employeeNumber";
        };

        String sortDirection = "ASC".equalsIgnoreCase(direction) ? "ASC" : "DESC";

        jpql.append(" ORDER BY ")
                .append(sortField)
                .append(" ")
                .append(sortDirection);

        TypedQuery<Employee> query = entityManager.createQuery(jpql.toString(), Employee.class);

        parameters.forEach(query::setParameter);

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();
    }

    public long countEmployees(
            String search,
            EmployeeDepartment department,
            EmployeeStatus status) {

        StringBuilder jpql = new StringBuilder("SELECT COUNT(e) FROM Employee e LEFT JOIN e.user u WHERE 1 = 1 ");

        Map<String, Object> parameters = new HashMap<>();

        if (search != null && !search.isBlank()) {

            jpql.append("AND (LOWER(e.employeeNumber) LIKE :search OR LOWER(e.firstName) LIKE :search OR LOWER(e.lastName) LIKE :search " +
                    "OR LOWER(e.email) LIKE :search OR LOWER(u.username) LIKE :search ) ");
            parameters.put("search", "%" + search.trim().toLowerCase() + "%");
        }

        if (department != null) {
            jpql.append(" AND e.department = :department ");
            parameters.put("department", department);
        }

        if (status != null) {
            jpql.append(" AND e.status = :status ");
            parameters.put("status", status);
        }

        TypedQuery<Long> query = entityManager.createQuery(jpql.toString(), Long.class);

        parameters.forEach(query::setParameter);

        return query.getSingleResult();
    }

    public Optional<Employee> findByIdWithDetails(UUID employeeId) {

        try {
            Employee employee = entityManager.createQuery("SELECT e FROM Employee e LEFT JOIN FETCH e.user u " +
                            "WHERE e.id = :id", Employee.class)
                    .setParameter("id", employeeId)
                    .getSingleResult();

            return Optional.of(employee);

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
