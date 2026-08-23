package com.globaltrade.logistics.ejb.messaging;

import com.globaltrade.logistics.core.dto.inventory.InventoryMonitorRecord;
import com.globaltrade.logistics.core.service.InventoryAlertService;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.*;

import java.util.logging.Level;
import java.util.logging.Logger;

@JMSDestinationDefinition(
        name = "java:global/jms/InventoryAlertQueue",
        interfaceName = "jakarta.jms.Queue",
        destinationName = "InventoryAlertQueue"
)
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(
                propertyName = "destinationLookup",
                propertyValue = "java:global/jms/InventoryAlertQueue"
        ),
        @ActivationConfigProperty(
                propertyName = "destinationType",
                propertyValue = "jakarta.jms.Queue"
        )
})
public class InventoryAlertMDB implements MessageListener {
    private static final Logger LOGGER = Logger.getLogger(InventoryAlertMDB.class.getName());
    @Inject
    private InventoryAlertService inventoryAlertService;

    @Override
    public void onMessage(Message message) {
        try {

            if (!(message instanceof ObjectMessage objectMessage)) {
                LOGGER.warning("InventoryAlertMDB received unsupported message type");
                return;
            }
            Object object = objectMessage.getObject();

            if (!(object instanceof InventoryMonitorRecord record)) {
                LOGGER.warning("InventoryAlertMDB received unsupported message payload");
                return;
            }

            LOGGER.info("InventoryAlertMDB received low-stock message: " + record.inventoryNumber());

            inventoryAlertService.handleLowStock(record);

        } catch (JMSException e) {
            LOGGER.log(Level.SEVERE, "Failed to process inventory JMS message", e);
            throw new RuntimeException(e);
        }
    }
}
