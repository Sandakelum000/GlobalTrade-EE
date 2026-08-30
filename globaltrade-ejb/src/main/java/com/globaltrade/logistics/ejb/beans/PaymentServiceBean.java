package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.annotation.Audited;
import com.globaltrade.logistics.core.dto.payment.PaymentRegistrationRequest;
import com.globaltrade.logistics.core.dto.payment.PaymentRegistrationResponse;
import com.globaltrade.logistics.core.entity.audit.AuditAction;
import com.globaltrade.logistics.core.entity.order.Order;
import com.globaltrade.logistics.core.entity.order.OrderStatus;
import com.globaltrade.logistics.core.entity.payment.Payment;
import com.globaltrade.logistics.core.entity.payment.PaymentMethod;
import com.globaltrade.logistics.core.entity.payment.PaymentStatus;
import com.globaltrade.logistics.core.exception.PaymentException;
import com.globaltrade.logistics.core.exception.ResourceNotFoundException;
import com.globaltrade.logistics.core.service.NumberSequenceService;
import com.globaltrade.logistics.core.service.PaymentService;
import com.globaltrade.logistics.core.service.ShipmentService;
import com.globaltrade.logistics.core.util.PayHereUtil;
import com.globaltrade.logistics.ejb.messaging.ShipmentCreationProducer;
import com.globaltrade.logistics.ejb.repository.OrderRepository;
import com.globaltrade.logistics.ejb.repository.PaymentRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;
import java.util.UUID;

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
    @Audited(
            action = AuditAction.PAYMENT,
            entity = "Payment"
    )
    @Transactional(Transactional.TxType.REQUIRED)
    public PaymentRegistrationResponse makePayment(UUID orderId,int paymentStatus) {
        if(orderId == null) {
            throw new PaymentException("Illegal payment request");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new PaymentException("Order id not found"));

        if(order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new PaymentException("This order is already CANCELLED");
        }
        if(order.getOrderStatus() != OrderStatus.PENDING) {
            throw new PaymentException("Payment can only be made on pending orders");
        }
        if(paymentRepository.findByOrderId(order.getId()).isPresent()) {
            throw new PaymentException("Payment already exists for order id " + order.getId());
        }
        String nextPaymentNumber = numberSequenceService.next(SEQUENCE_KEY, PREFIX, WIDTH);

        Payment payment = Payment.builder()
                .paymentNumber(nextPaymentNumber)
                .order(order)
                .totalAmount(order.getTotalAmount())
                .paymentMethod(PaymentMethod.ONLINE)
                .paymentStatus(paymentStatus == PayHereUtil.PAYMENT_SUCCESS ? PaymentStatus.PAID: PaymentStatus.FAILED)
                .paidAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);
        order.setOrderStatus(paymentStatus == PayHereUtil.PAYMENT_SUCCESS ? OrderStatus.CONFIRMED : OrderStatus.CANCELLED);

        if(paymentStatus == PayHereUtil.PAYMENT_SUCCESS) {
            shipmentCreationProducer.send(order.getId());
        }

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
