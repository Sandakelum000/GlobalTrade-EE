package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.common.Country;
import com.globaltrade.logistics.core.entity.company.Company;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ContentRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Country> getCountries() {
        return entityManager.createQuery("select c from Country c", Country.class)
                .getResultList();
    }

    public Optional<Country> getCountryById(UUID countryId) {
        return Optional.ofNullable(entityManager.find(Country.class, countryId));
    }

    public List<Company> getCompaniesByCountryId(UUID countryId) {
        return entityManager.createNamedQuery("Company.findByCountryId", Company.class)
                .setParameter("countryId", countryId)
                .getResultList();
    }
}
