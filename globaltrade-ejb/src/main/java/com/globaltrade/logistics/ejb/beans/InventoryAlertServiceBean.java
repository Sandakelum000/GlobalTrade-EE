package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.dto.inventory.InventoryMonitorRecord;
import com.globaltrade.logistics.core.entity.audit.AuditAction;
import com.globaltrade.logistics.core.entity.audit.AuditLog;
import com.globaltrade.logistics.core.service.AuditLogService;
import com.globaltrade.logistics.core.service.InventoryAlertService;
import com.globaltrade.logistics.ejb.repository.AuditRepository;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.logging.Logger;

@Stateless
public class InventoryAlertServiceBean implements InventoryAlertService {
    private static final Logger LOGGER = Logger.getLogger(InventoryAlertServiceBean.class.getName());

    @Inject
    private AuditRepository auditRepository;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void handleLowStock(InventoryMonitorRecord record) {
        String details =
                "LOW STOCK ALERT"
                        + " | Inventory=" + record.inventoryNumber()
                        + " | Product=" + record.productNumber()
                        + " | Warehouse=" + record.warehouseName()
                        + " | Available=" + record.availableQuantity()
                        + " | Reorder Level=" + record.recorderLevel();

        LOGGER.warning(details);

        AuditLog auditLog = AuditLog.builder()
                .user(null)
                .action(AuditAction.UPDATE)
                .entityType("Inventory")
                .entityId(record.inventoryNumber())
                .actionTimestamp(LocalDateTime.now())
                .description(details)
                .build();
        auditRepository.save(auditLog);
    }
}
