package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.employee.Employee;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class EmployeeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(Employee employee) {
        entityManager.persist(employee);
    }

    public Optional<Employee> findEmployeeByNumber(String employeeNumber) {
        try{
            return Optional.of(entityManager.createNamedQuery("Employee.findByNumber", Employee.class)
                    .setParameter("employeeNumber", employeeNumber)
                    .getSingleResult());
        }catch (NoResultException e){
            return Optional.empty();
        }
    }

    public Optional<Employee> findById(UUID employeeId) {
        return Optional.ofNullable(entityManager.find(Employee.class, employeeId));
    }

    public Optional<Employee> findByEmail(String email) {
       try{
           return Optional.of(entityManager.createNamedQuery("Employee.findByEmail", Employee.class)
                   .setParameter("email", email)
                   .getSingleResult());
       }catch (NoResultException e){
           return Optional.empty();
       }
    }
}
