package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.customer.Customer;
import com.globaltrade.logistics.core.entity.vendor.Vendor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class VendorRepository {
    @PersistenceContext
    private EntityManager em;

    public void save(Vendor vendor) {
        em.persist(vendor);
        em.flush();
    }

    public Optional<Vendor> findById(UUID id) {
        try {
            Vendor vendor = em.find(Vendor.class, id);
            return Optional.ofNullable(vendor);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public  Optional<Vendor> findByUserId(UUID userId) {
        try{
            return Optional.of(em.createQuery("SELECT v FROM Vendor v JOIN FETCH v.user " +
                            "JOIN FETCH v.company WHERE v.user.id =:userId",Vendor.class)
                    .setParameter("userId", userId)
                    .getSingleResult());
        }catch (NoResultException e){
            return Optional.empty();
        }
    }

    public boolean existsByUserId(UUID userId) {
        Long count = em.createQuery("SELECT COUNT(v) FROM Vendor v WHERE v.user.id=:userId", Long.class)
                .setParameter("userId", userId)
                .getSingleResult();

        return count > 0;
    }
}
