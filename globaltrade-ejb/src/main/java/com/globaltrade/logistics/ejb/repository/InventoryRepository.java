package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.dto.admin.dashboard.LowStockResponse;
import com.globaltrade.logistics.core.entity.warehouse.Inventory;
import com.globaltrade.logistics.core.entity.warehouse.InventoryStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;
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

    public List<Inventory> findLowStock() {
        return entityManager.createNamedQuery("Inventory.findLowStock",Inventory.class)
                .setParameter("status", InventoryStatus.ACTIVE)
                .getResultList();
    }


    public List<Inventory> findAvailableInventory() {
        return entityManager.createQuery(" SELECT i FROM Inventory i JOIN FETCH i.product " +
                "JOIN FETCH i.warehouse WHERE i.status=:status AND (i.quantity - i.reservedQuantity) > 0 " +
                "ORDER BY i.product.title",Inventory.class)
                .setParameter("status",InventoryStatus.ACTIVE)
                .getResultList();
    }

    //admin
    public long countLowStock() {
        try{
            return entityManager.createQuery("SELECT COUNT(i) FROM Inventory i " +
                            "WHERE (i.quantity - i.reservedQuantity) <= i.reorderLevel " +
                            "AND i.status = :status", Long.class)
                    .setParameter("status", InventoryStatus.ACTIVE)
                    .getSingleResult();
        }catch(NoResultException ex){
            return 0;
        }
    }

    public List<LowStockResponse> findLowStockRecords(int limit) {
        return entityManager.createQuery(
                        """
                        SELECT i
                        FROM Inventory i
                        WHERE (i.quantity - i.reservedQuantity) <= i.reorderLevel
                        AND i.status = :status
                        ORDER BY (i.quantity - i.reservedQuantity) ASC
                        """,
                        Inventory.class
                )
                .setParameter("status", InventoryStatus.ACTIVE)
                .setMaxResults(limit)
                .getResultList()
                .stream()
                .map(i -> new LowStockResponse(
                        i.getId(),
                        i.getInventoryNumber(),
                        i.getProduct().getId(),
                        i.getProduct().getTitle(),
                        i.getWarehouse().getId(),
                        i.getWarehouse().getName(),
                        i.getAvailableQuantity(),
                        i.getReorderLevel()
                ))
                .toList();
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
