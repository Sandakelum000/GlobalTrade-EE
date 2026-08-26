package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.product.Product;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ProductRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(Product product) {
        entityManager.persist(product);
    }

    public boolean existsByTitle(String title) {
        Long count = entityManager.createNamedQuery("Product.existsByTitle", Long.class)
                .setParameter("title", title).getSingleResult();
        return count > 0;
    }

    public long countAll() {
        try {
            return entityManager.createQuery("SELECT COUNT(p) FROM Product p",
                            Long.class)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return 0;
        }
    }

    public Optional<Product> findByProductNumber(String productNumber) {
        try {
            return Optional.of(entityManager.createNamedQuery("Product.findByProductNumber", Product.class)
                    .setParameter("productNumber", productNumber)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public Optional<Product> findProductById(UUID productId) {
        return Optional.ofNullable(entityManager.find(Product.class, productId));
    }


}
