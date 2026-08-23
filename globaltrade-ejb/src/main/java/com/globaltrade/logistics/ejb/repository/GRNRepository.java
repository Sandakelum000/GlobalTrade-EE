package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.grn.GRNStatus;
import com.globaltrade.logistics.core.entity.grn.GoodsReceiveNote;
import com.globaltrade.logistics.core.entity.vendor.Vendor;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class GRNRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(GoodsReceiveNote grn) {
        entityManager.persist(grn);
    }

    public Optional<GoodsReceiveNote> findByGrnId(UUID grnId) {
        try{
            return Optional.of(entityManager.find(GoodsReceiveNote.class, grnId));
        }catch(IllegalArgumentException e){
            return Optional.empty();
        }
    }


    public long countByVendor(UUID vendorId) {
        try{
            return entityManager.createQuery("SELECT COUNT(g) FROM GoodsReceiveNote g WHERE g.vendor.id=:vendorId", Long.class)
                    .setParameter("vendorId",vendorId)
                    .getSingleResult();
        }catch (NoResultException e){
            return 0;
        }
    }

    public long successfulCountByVendor(UUID vendorId) {
        try{
            return entityManager.createQuery("SELECT COUNT(g) FROM GoodsReceiveNote g" +
                            " WHERE g.vendor.id=:vendorId " + "AND g.status=:status", Long.class)
                    .setParameter("vendorId", vendorId)
                    .setParameter("status", GRNStatus.RECEIVED)
                    .getSingleResult();
        }catch(NoResultException e){
            return 0;
        }
    }

    public Optional<GoodsReceiveNote> findByGrnNumber(String grnNumber) {
        try{
            return Optional.of(entityManager.createNamedQuery("GRN.findByGrnNumber", GoodsReceiveNote.class)
                    .setParameter("grnNumber", grnNumber).getSingleResult());
        }catch (NoResultException e){
            return Optional.empty();
        }
    }

}
