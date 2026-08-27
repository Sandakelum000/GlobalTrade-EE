package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.product.Product;
import com.globaltrade.logistics.core.entity.warehouse.Inventory;
import com.globaltrade.logistics.core.entity.warehouse.Warehouse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.*;

@ApplicationScoped
public class AdminInventoryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Inventory> findInventories(
            String search,
            UUID warehouseId,
            UUID productId,
            Boolean lowStock,
            Boolean outOfStock,
            String sortBy,
            String direction,
            int page,
            int size
    ) {
        StringBuilder jpql = new StringBuilder("SELECT i FROM Inventory i JOIN i.product p " +
                "JOIN i.warehouse w WHERE 1 = 1 ");

        Map<String, Object> parameters = new HashMap<>();

        if (search != null && !search.isBlank()) {
            jpql.append(" AND (LOWER(i.inventoryNumber) LIKE :search OR LOWER(p.title) LIKE :search)");
            parameters.put("search", "%" + search.trim().toLowerCase() + "%");
        }

        if (warehouseId != null) {
            jpql.append(" AND w.id = :warehouseId ");
            parameters.put("warehouseId", warehouseId);
        }
        if (productId != null) {
            jpql.append(" AND p.id = :productId ");
            parameters.put("productId", productId);
        }
        if (Boolean.TRUE.equals(outOfStock)) {
            jpql.append(" AND (i.quantity - i.reservedQuantity) <=0 ");
        } else if (Boolean.TRUE.equals(lowStock)) {
            jpql.append(" AND (i.quantity - i.reservedQuantity) <= i.reorderLevel ");
        }

        String sortField = switch (sortBy == null ? "updatedAt" : sortBy) {
            case "inventoryNumber" -> "i.inventoryNumber";
            case "productName" -> "p.title";
            case "warehouseName" -> "w.name";
            case "quantity" -> "i.quantity";
            case "reservedQuantity" -> "i.reservedQuantity";
            case "availableQuantity" ->
                    "(i.quantity - i.reservedQuantity)";
            case "reorderLevel" -> "i.reorderLevel";
            default -> "i.updatedAt";
        };

        String sortDirection = "ASC".equalsIgnoreCase(direction) ? "ASC" : "DESC";

        jpql.append(" ORDER BY ")
                .append(sortField)
                .append(" ")
                .append(sortDirection);

        TypedQuery<Inventory> query = entityManager.createQuery(jpql.toString(), Inventory.class);

        parameters.forEach(query::setParameter);

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return query.getResultList();

    }

    public long countInventories(
            String search,
            UUID warehouseId,
            UUID productId,
            Boolean lowStock,
            Boolean outOfStock
    ) {

        StringBuilder jpql = new StringBuilder("""
                SELECT COUNT(i)
                FROM Inventory i
                JOIN i.product p
                JOIN i.warehouse w
                WHERE 1 = 1
                """);

        Map<String, Object> parameters = new HashMap<>();

        if (search != null && !search.isBlank()) {

            jpql.append("""
                    AND (
                        LOWER(i.inventoryNumber) LIKE :search
                        OR LOWER(p.name) LIKE :search
                    )
                    """);

            parameters.put(
                    "search",
                    "%" + search.trim().toLowerCase() + "%"
            );
        }

        if (warehouseId != null) {

            jpql.append(
                    " AND w.id = :warehouseId "
            );

            parameters.put(
                    "warehouseId",
                    warehouseId
            );
        }

        if (productId != null) {

            jpql.append(
                    " AND p.id = :productId "
            );

            parameters.put(
                    "productId",
                    productId
            );
        }

        if (Boolean.TRUE.equals(outOfStock)) {

            jpql.append("""
                    AND (i.quantity - i.reservedQuantity) <= 0
                    """);

        } else if (Boolean.TRUE.equals(lowStock)) {

            jpql.append("""
                    AND (i.quantity - i.reservedQuantity)
                        <= i.reorderLevel
                    """);
        }

        TypedQuery<Long> query =
                entityManager.createQuery(
                        jpql.toString(),
                        Long.class
                );

        parameters.forEach(query::setParameter);

        return query.getSingleResult();
    }

    public List<Warehouse> findWarehouseOptions() {
        return entityManager.createQuery("SELECT w FROM Warehouse w ORDER BY w.name ASC", Warehouse.class)
                .getResultList();
    }

    public List<Product> findProductOptions(String search) {
        if (search == null || search.isBlank()) {
            return entityManager.createQuery("SELECT p FROM Product p ORDER BY p.title ASC", Product.class)
                    .setMaxResults(20)
                    .getResultList();
        }

        return entityManager.createQuery("SELECT p FROM Product p WHERE LOWER(p.title) " +
                        "LIKE :search ORDER BY p.title ASC", Product.class)
                .setParameter("search", "%" + search.trim().toLowerCase() + "%")
                .setMaxResults(20)
                .getResultList();
    }

    public Optional<Inventory> findDetailsById(UUID inventoryId) {

        List<Inventory> results = entityManager.createQuery("""
            SELECT i
            FROM Inventory i
            JOIN FETCH i.product
            JOIN FETCH i.warehouse
            JOIN FETCH i.grnItem gi
            WHERE i.id = :inventoryId
            """, Inventory.class)
                .setParameter("inventoryId", inventoryId)
                .setMaxResults(1)
                .getResultList();

        return results.stream().findFirst();
    }
}
