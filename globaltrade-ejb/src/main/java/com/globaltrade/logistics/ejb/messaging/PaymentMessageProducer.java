package com.globaltrade.logistics.ejb.messaging;

import com.globaltrade.logistics.core.dto.payment.PaymentRegistrationRequest;
import com.globaltrade.logistics.core.entity.payment.Payment;
import com.globaltrade.logistics.core.service.CheckoutMessageService;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;

import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class PaymentMessageProducer implements CheckoutMessageService {

    private static final Logger LOGGER = Logger.getLogger(PaymentMessageProducer.class.getName());

    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = "java:global/jms/PaymentQueue")
    private Queue queue;

    @Override
    public void sendPayload(PaymentRegistrationRequest payload) {
        if(payload == null) return;

        jmsContext.createProducer().send(queue, payload);
        LOGGER.log(Level.INFO, "Payment message sent to orderId", payload.orderId());
    }
}
