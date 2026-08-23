package com.globaltrade.logistics.ejb.messaging;

import com.globaltrade.logistics.core.dto.shipment.ShipmentRegistrationRequest;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;

import java.util.UUID;

@Stateless
public class ShipmentCreationProducer {
    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = "java:global/jms/ShipmentCreationQueue")
    private Queue shipmentCreationQueue;

    public void send(UUID orderId){
        jmsContext.createProducer()
                .send(shipmentCreationQueue,new ShipmentRegistrationRequest(orderId));
    }
}
