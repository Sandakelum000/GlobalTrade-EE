package com.globaltrade.logistics.ejb.messaging;

import com.globaltrade.logistics.core.dto.shipment.ShipmentRegistrationRequest;
import com.globaltrade.logistics.core.service.ShipmentService;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.JMSDestinationDefinition;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@JMSDestinationDefinition(
        name = "java:global/jms/ShipmentCreationQueue",
        interfaceName = "jakarta.jms.Queue",
        destinationName = "ShipmentCreationQueue"
)
@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(
                        propertyName = "destinationType",
                        propertyValue = "jakarta.jms.Queue"
                ),
                @ActivationConfigProperty(
                        propertyName = "destinationLookup",
                        propertyValue = "java:global/jms/ShipmentCreationQueue"
                )
        }
)
public class ShipmentCreationMDB implements MessageListener {

    @Inject
    private ShipmentService shipmentService;

    private static final Logger LOGGER = Logger.getLogger(ShipmentCreationMDB.class.getName());

    @Override
    public void onMessage(Message message) {
        try{
            ShipmentRegistrationRequest shipmentMessage = message.getBody(ShipmentRegistrationRequest.class);
            UUID orderId = shipmentMessage.orderId();

            LOGGER.info("Received Shipment request for: Order ID: " + orderId);

            shipmentService.createShipmentsForOrder(orderId); //async shipment automation

        }catch (Exception e){
            LOGGER.log(Level.WARNING, e.getMessage(), "shipment creation failed"+e.getMessage());
            throw  new RuntimeException(e);
        }
    }
}
