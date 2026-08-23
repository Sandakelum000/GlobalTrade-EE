package com.globaltrade.logistics.ejb.timer;

import com.globaltrade.logistics.core.dto.inventory.InventoryMonitorRecord;
import com.globaltrade.logistics.core.service.InventoryService;
import jakarta.annotation.Resource;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@Startup
public class InventoryMonitoringTimer {
    private static final Logger LOGGER =
            Logger.getLogger(InventoryMonitoringTimer.class.getName());

    @Inject
    private InventoryService inventoryService;

    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = "java:global/jms/InventoryAlertQueue")
    private Queue inventoryAlertQueue;

    @Schedule(hour = "*", minute = "*/10")
    private void monitorInventory() {
        LOGGER.info("Inventory Monitoring Timer Started...");
        try {
            List<InventoryMonitorRecord> lowStockList = inventoryService.findLowStock();
            if (lowStockList.isEmpty()) {
                LOGGER.info("Inventory Monitoring: No low stock inventory found");
                return;
            }

            for (InventoryMonitorRecord record : lowStockList) {
                LOGGER.warning("Low Stock Detected: " + record.inventoryNumber());

                jmsContext.createProducer().send(inventoryAlertQueue, record);

                LOGGER.info("Low-stock JMS message sent: " + record.inventoryNumber());
            }
            LOGGER.info("Inventory Monitoring completed. " + lowStockList.size() + " low-stock items found.");


        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Inventory Monitoring Timer Failed.", e);
        }
    }

}
