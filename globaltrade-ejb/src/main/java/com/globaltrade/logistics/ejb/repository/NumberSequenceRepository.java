package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.common.NumberSequence;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;

@ApplicationScoped
public class NumberSequenceRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public void save(NumberSequence numberSequence) {
        entityManager.persist(numberSequence);
        entityManager.flush();
    }

    public Optional<NumberSequence> findByKey(String sequenceKey) {
        try{
            return Optional.of(
                    entityManager.createNamedQuery("NumberSequence.findByKey", NumberSequence.class)
                            .setParameter("sequenceKey", sequenceKey)
                            .getSingleResult()
            );
        }catch(NoResultException e){
            return Optional.empty();
        }
    }

    public Optional<NumberSequence> findByKeyForUpdate(String sequenceKey) {
        try{
            return Optional.of(entityManager.createNamedQuery("NumberSequence.findByKey", NumberSequence.class)
                    .setParameter("sequenceKey", sequenceKey)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getSingleResult());
        }catch (NoResultException e){
            return Optional.empty();
        }
    }
}
