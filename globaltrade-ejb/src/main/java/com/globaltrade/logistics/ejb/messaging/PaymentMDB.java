package com.globaltrade.logistics.ejb.messaging;

import com.globaltrade.logistics.core.dto.payment.PaymentRegistrationRequest;
import com.globaltrade.logistics.core.dto.payment.PaymentRegistrationResponse;
import com.globaltrade.logistics.core.service.PaymentService;
import com.globaltrade.logistics.core.util.PayHereUtil;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSDestinationDefinition;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;

import java.util.UUID;

@JMSDestinationDefinition(
        name = "java:global/jms/PaymentQueue",
        interfaceName = "jakarta.jms.Queue",
        destinationName = "PaymentQueue"
)
@MessageDriven(
        activationConfig = {
                @ActivationConfigProperty(
                        propertyName = "destinationLookup",
                        propertyValue = "java:global/jms/PaymentQueue"
                ),
                @ActivationConfigProperty(
                        propertyName = "destinationType",
                        propertyValue = "jakarta.jms.Queue"
                )
        }
)
public class PaymentMDB implements MessageListener {

    @EJB
    private PaymentService paymentService;

    @Override
    public void onMessage(Message message) {
        try {
            PaymentRegistrationRequest paymentData = message.getBody(PaymentRegistrationRequest.class);
            UUID orderId = paymentData.orderId();
            int statusCode = paymentData.statusCode();

            PaymentRegistrationResponse response = paymentService.makePayment(orderId,statusCode);
            System.out.println(response.toString());


        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
