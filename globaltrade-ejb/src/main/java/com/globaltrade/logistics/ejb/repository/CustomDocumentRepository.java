package com.globaltrade.logistics.ejb.repository;

import com.globaltrade.logistics.core.entity.order.shipment.CustomsDocument;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CustomDocumentRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public CustomsDocument save(CustomsDocument customsDocument) {
       if(customsDocument.getId() == null) {
           entityManager.persist(customsDocument);
           return customsDocument;
       }
       return entityManager.merge(customsDocument);
    }

    public Optional<CustomsDocument> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(CustomsDocument.class, id));
    }

    public  List<CustomsDocument> findByShipmentId(UUID shipmentId) {
        return entityManager.createQuery("SELECT c FROM CustomsDocument c WHERE c.shipment.id = :shipmentId", CustomsDocument.class)
                .setParameter("shipmentId", shipmentId)
                .getResultList();
    }

    public List<CustomsDocument> findByShipmentIdDESC(UUID shipmentId) {
        return entityManager.createNamedQuery("CustomDocument.findByShipmentId", CustomsDocument.class)
                .setParameter("shipmentId", shipmentId)
                .getResultList();
    }

    public void delete(CustomsDocument customsDocument) {
        entityManager.remove(entityManager.contains(customsDocument)
                ? customsDocument
                : entityManager.merge(customsDocument));
    }


}
