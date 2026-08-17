package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.company.Company;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CompanyRepository {
    @PersistenceContext
    private EntityManager em;

    public Company save(Company company) {
        em.persist(company);
        return company;
    }

    public Optional<Company> findById(UUID id) {
        try{
            return Optional.of(
                    em.createNamedQuery("Company.findByCompanyId",Company.class)
                            .setParameter("companyId",id)
                            .getSingleResult()
            );
        }catch(NoResultException e){
            return Optional.empty();
        }
    }


    public Optional<Company> findByRegistrationNumber(String companyNumber) {
        try{
            return Optional.of(
                    em.createNamedQuery("Company.findByCompanyNumber",Company.class)
                            .setParameter("registrationNumber",companyNumber)
                    .getSingleResult()
            );
        }catch(NoResultException e){
            return Optional.empty();
        }
    }

    public boolean existsByRegistrationNumber(String companyNumber) {
        return findByRegistrationNumber(companyNumber).isPresent();
    }

}
