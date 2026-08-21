package com.globaltrade.logistics.ejb.timer;

import com.globaltrade.logistics.core.dto.inventory.InventoryMonitorRecord;
import com.globaltrade.logistics.core.service.InventoryService;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

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

    @Schedule(hour = "*",minute = "*/10")
    private void monitorInventory() {
        LOGGER.info("Inventory Monitoring Timer Started...");
        try{
            List<InventoryMonitorRecord> lowStockList = inventoryService.findLowStock();
            if(lowStockList.isEmpty()){
                LOGGER.info("Inventory Monitoring: No low stock inventory found");
                return;
            }

            for(InventoryMonitorRecord record : lowStockList){
                LOGGER.warning(
                        "LOW STOCK: InventoryId"
                                + record.inventoryId()
                                +", inventoryNumber"
                                +record.inventoryNumber()
                                + ", ProductId="
                                + record.productId()
                                +" productNumber"
                                +record.productNumber()
                                + ", Warehouse="
                                + record.warehouseName()
                                + ", Available="
                                + record.availableQuantity()
                                + ", Reorder Level="
                                + record.recorderLevel()
                );
                LOGGER.info("Inventory monitoring completed. " + lowStockList.size() + " low_stock items found.");
            }


        }catch(Exception e){
            LOGGER.log(Level.SEVERE, "Inventory Monitoring Timer Failed.", e);
        }
    }

}
