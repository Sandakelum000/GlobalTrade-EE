package com.globaltrade.logistics.ejb.messaging;

import com.globaltrade.logistics.core.dto.grn.GRNReceivedEvent;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;

@ApplicationScoped
public class GRNEventPublisher {
    @Resource(lookup = "jms/GlobalTradeConnectionFactory")
    private JMSContext jmsContext;

    @Resource(lookup = "jms/GRNReceivedQueue")
    private Queue grnReceivedQueue;

    public void publish(GRNReceivedEvent event) {
        jmsContext.createProducer().send(grnReceivedQueue, event);
    }
}
