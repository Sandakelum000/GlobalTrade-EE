package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.annotation.Audited;
import com.globaltrade.logistics.core.dto.payment.PaymentRegistrationRequest;
import com.globaltrade.logistics.core.dto.payment.PaymentRegistrationResponse;
import com.globaltrade.logistics.core.entity.audit.AuditAction;
import com.globaltrade.logistics.core.entity.order.Order;
import com.globaltrade.logistics.core.entity.order.OrderStatus;
import com.globaltrade.logistics.core.entity.payment.Payment;
import com.globaltrade.logistics.core.entity.payment.PaymentStatus;
import com.globaltrade.logistics.core.service.NumberSequenceService;
import com.globaltrade.logistics.core.service.PaymentService;
import com.globaltrade.logistics.core.service.ShipmentService;
import com.globaltrade.logistics.ejb.messaging.ShipmentCreationProducer;
import com.globaltrade.logistics.ejb.repository.OrderRepository;
import com.globaltrade.logistics.ejb.repository.PaymentRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@Stateless
public class PaymentServiceBean implements PaymentService {
    private static final String SEQUENCE_KEY = "PAYMENT";
    private static final String PREFIX = "PAY";
    private static final int WIDTH = 3;

    @Inject
    private PaymentRepository  paymentRepository;
    @Inject
    private OrderRepository orderRepository;
    @Inject
    private NumberSequenceService numberSequenceService;
    @Inject
    private ShipmentCreationProducer shipmentCreationProducer;

    @Override
    @RolesAllowed({"CUSTOMER","ADMIN"})
    @Audited(
            action = AuditAction.CREATE,
            entity = "Payment"
    )
    @Transactional(Transactional.TxType.REQUIRED)
    public PaymentRegistrationResponse makePayment(PaymentRegistrationRequest request) {
        if(request == null) {
            throw new IllegalArgumentException("Illegal payment request");
        }

        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new IllegalArgumentException("Order id not found"));

        if(order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("This order is already CANCELLED");
        }
        if(order.getOrderStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("Payment can only be made on pending orders");
        }
        if(paymentRepository.findByOrderId(order.getId()).isPresent()) {
            throw new IllegalArgumentException("Payment already exists for order id " + order.getId());
        }
        String nextPaymentNumber = numberSequenceService.next(SEQUENCE_KEY, PREFIX, WIDTH);

        Payment payment = Payment.builder()
                .paymentNumber(nextPaymentNumber)
                .order(order)
                .totalAmount(order.getTotalAmount())
                .paymentMethod(request.paymentMethod())
                .paymentStatus(PaymentStatus.PAID)
                .paidAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);
        order.setOrderStatus(OrderStatus.CONFIRMED);

        shipmentCreationProducer.send(order.getId());

        return toResponse(payment);
    }

    private PaymentRegistrationResponse toResponse(@NonNull Payment payment) {
        return new PaymentRegistrationResponse(
                payment.getId(),
                payment.getPaymentNumber(),
                payment.getOrder().getId(),
                payment.getOrder().getOrderNumber(),
                payment.getTotalAmount(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                payment.getPaidAt()
        );
    }
}
