package com.globaltrade.logistics.ejb.messaging;

import com.globaltrade.logistics.core.dto.grn.GRNReceivedEvent;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.ObjectMessage;

@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(
                        propertyName = "destinationType",
                        propertyValue = "jakarta.jms.Queue"
                ),
                @ActivationConfigProperty(
                        propertyName = "destinationLookup",
                        propertyValue = "jms/GRNReceivedQueue"
                )
        }
)
public class GRNReceivedMessageBean implements MessageListener {
    @Override
    public void onMessage(Message message) {

        try {

            ObjectMessage objectMessage = (ObjectMessage) message;
            GRNReceivedEvent event =
                    (GRNReceivedEvent) objectMessage.getObject();

            System.out.println("GRN RECEIVED EVENT: " + event.grnNumber());

        } catch (Exception e) {
            throw new RuntimeException("Failed to process GRN received event", e);
        }
    }
}