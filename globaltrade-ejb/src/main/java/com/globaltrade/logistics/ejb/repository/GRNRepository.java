package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.grn.GoodsReceiveNote;
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

    public Optional<GoodsReceiveNote> findByGrnNumber(String grnNumber) {
        try{
            return Optional.of(entityManager.createNamedQuery("GRN.findByGrnNumber", GoodsReceiveNote.class)
                    .setParameter("grnNumber", grnNumber).getSingleResult());
        }catch (NoResultException e){
            return Optional.empty();
        }
    }

}
