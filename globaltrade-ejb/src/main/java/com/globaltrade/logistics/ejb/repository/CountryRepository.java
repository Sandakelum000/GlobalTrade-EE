package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.common.Country;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CountryRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Country> getCountryById(UUID countryId) {
        return Optional.ofNullable(entityManager.find(Country.class, countryId));
    }
}
