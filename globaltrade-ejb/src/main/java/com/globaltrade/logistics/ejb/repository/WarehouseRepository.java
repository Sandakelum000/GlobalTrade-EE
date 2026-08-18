package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.warehouse.Warehouse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class WarehouseRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Warehouse> getWarehouseById(UUID warehouseId) {
        try{
            return Optional.of(entityManager.find(Warehouse.class, warehouseId));
        }catch (IllegalArgumentException e){
            return Optional.empty();
        }
    }
}
