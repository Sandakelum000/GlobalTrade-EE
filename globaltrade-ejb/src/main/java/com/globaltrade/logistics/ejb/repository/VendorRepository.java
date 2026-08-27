package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.customer.Customer;
import com.globaltrade.logistics.core.entity.vendor.Vendor;
import com.globaltrade.logistics.core.entity.vendor.VendorStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;
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
        return Optional.ofNullable(em.find(Vendor.class, id));
    }

    public Optional<Vendor> findByUserId(UUID userId) {
        try {
            return Optional.of(em.createQuery("SELECT v FROM Vendor v JOIN FETCH v.user " +
                            "JOIN FETCH v.company WHERE v.user.id =:userId", Vendor.class)
                    .setParameter("userId", userId)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public long countAll() {
        try {
            return em.createQuery("SELECT COUNT(v) FROM Vendor v",
                            Long.class)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0;
        }
    }

    public List<Vendor> findByCompanyId(UUID companyId) {
        return em.createQuery("SELECT v FROM Vendor v WHERE v.company.id=:companyId " +
                "AND v.status=:status ORDER BY v.contactFirstName ASC,v.contactLastName ASC",Vendor.class)
                .setParameter("companyId", companyId)
                .setParameter("status", VendorStatus.ACTIVE)
                .getResultList();
    }

    public Optional<Vendor> findByVendorNumber(String vendorNumber) {
        try {
            return Optional.of(em.createNamedQuery("Vendor.findByVendorNumber", Vendor.class)
                    .setParameter("vendorNumber", vendorNumber)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public boolean existsByUserId(UUID userId) {
        Long count = em.createQuery("SELECT COUNT(v) FROM Vendor v WHERE v.user.id=:userId", Long.class)
                .setParameter("userId", userId)
                .getSingleResult();

        return count > 0;
    }

    public List<Vendor> findActiveVendors() {
        return em.createNamedQuery("Vendor.findActiveVendors", Vendor.class)
                .setParameter("status", VendorStatus.ACTIVE)
                .getResultList();
    }
}
