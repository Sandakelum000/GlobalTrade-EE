package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.warehouse.Inventory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class InventoryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(Inventory inventory) {
        entityManager.persist(inventory);
    }

    public Optional<Inventory> findByIdForUpdate(UUID inventoryId) {
        try{
            return Optional.of(
                    entityManager.createNamedQuery("Inventory.findById",Inventory.class)
                            .setParameter("id",inventoryId)
                            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                            .getSingleResult()
            );
        }catch(NoResultException ex){
            return Optional.empty();
        }
    }

    public Optional<Inventory> findByWarehouseAndProduct(UUID warehouseId, UUID productId) {
        try {
            return Optional.of(entityManager.createNamedQuery("Inventory.findByWarehouseAndProduct", Inventory.class)
                    .setParameter("warehouseId", warehouseId)
                    .setParameter("productId", productId)
                    .getSingleResult());

        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Inventory> findByWarehouseAndProductForUpdate(UUID warehouseId, UUID productId) {
        try {
            return Optional.of(
                    entityManager.createNamedQuery("Inventory.findByWarehouseAndProduct", Inventory.class)
                            .setParameter("warehouseId", warehouseId)
                            .setParameter("productId", productId)
                            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
