package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.annotation.Audited;
import com.globaltrade.logistics.core.entity.audit.AuditAction;
import com.globaltrade.logistics.core.entity.customer.Customer;
import com.globaltrade.logistics.core.entity.order.Order;
import com.globaltrade.logistics.core.entity.order.OrderItem;
import com.globaltrade.logistics.core.entity.order.OrderStatus;
import com.globaltrade.logistics.core.entity.payment.PayHereDTO;
import com.globaltrade.logistics.core.exception.CheckoutException;
import com.globaltrade.logistics.core.exception.ResourceNotFoundException;
import com.globaltrade.logistics.core.service.CheckoutService;
import com.globaltrade.logistics.core.service.ConfigService;
import com.globaltrade.logistics.core.util.PayHereUtil;
import com.globaltrade.logistics.ejb.repository.OrderRepository;
import com.globaltrade.logistics.ejb.repository.PaymentRepository;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.Locale;
import java.util.UUID;
import java.util.logging.Logger;

@Stateless
@Transactional(Transactional.TxType.REQUIRED)
public class CheckoutServiceBean implements CheckoutService {
    private static final Logger LOGGER = Logger.getLogger(CheckoutServiceBean.class.getName());

    @Inject
    private OrderRepository orderRepository;
    @Inject
    private PaymentRepository paymentRepository;
    @EJB
    private ConfigService configService;

    @Override
    public PayHereDTO processCheckout(UUID orderId) {
        if(orderId == null) {
            throw new CheckoutException("Illegal payment request");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CheckoutException("Order id not found"));

        if(order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new CheckoutException("This order is already CANCELLED");
        }
        if(order.getOrderStatus() != OrderStatus.PENDING) {
            throw new CheckoutException("Payment can only be made on pending orders");
        }
        if(paymentRepository.findByOrderId(order.getId()).isPresent()) {
            throw new CheckoutException("Payment already exists for order id " + order.getId());
        }

        return createPaymentDetails(order);

    }

    private PayHereDTO createPaymentDetails(Order order) {
        LOGGER.info("Creating payment details for order id " + order.getId());

        String returnURL = configService.get("app.public.url") + "/api/payments/return";
        String cancelURL = configService.get("app.public.url") + "/api/payments/cancel";
        String notifyURL = configService.get("app.public.url") + "/api/payments/notify";

        String orderId = order.getId().toString();
        Customer customer = order.getCustomer();

        StringBuilder items = new StringBuilder();
        for(OrderItem  orderItem : order.getItems()) {
            if(!items.isEmpty()){
                items.append(", ");
            }
            items.append(orderItem.getInventory().getProduct().getTitle())
                    .append(" x ")
                    .append(orderItem.getQuantity());
        }
        String formattedAmount = String.format(Locale.US, "%.2f", order.getTotalAmount());
        String hashValue = PayHereUtil.generateHash(orderId, formattedAmount);

        PayHereDTO payHereDTO = new PayHereDTO();
        payHereDTO.setSandbox(true);
        payHereDTO.setMerchant_id(PayHereUtil.getMerchantId());
        payHereDTO.setReturn_url(returnURL);
        payHereDTO.setCancel_url(cancelURL);
        payHereDTO.setNotify_url(notifyURL);

        payHereDTO.setOrder_id(orderId);
        payHereDTO.setItems(items.toString());
        payHereDTO.setAmount(formattedAmount);
        payHereDTO.setCurrency(PayHereUtil.APP_CURRENCY);
        payHereDTO.setHash(hashValue);
        payHereDTO.setFirst_name(customer.getFirstName());
        payHereDTO.setLast_name(customer.getLastName());
        payHereDTO.setEmail(customer.getEmail());
        payHereDTO.setPhone(customer.getMobile1());
        payHereDTO.setAddress(customer.getCompany().getAddress().getLine1()+" "+customer.getCompany().getAddress().getLine2());
        payHereDTO.setCity(customer.getCompany().getAddress().getCity());
        payHereDTO.setCountry(customer.getCompany().getAddress().getCountry().getName());

        return payHereDTO;
    }
}
