package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.entity.common.Address;
import com.globaltrade.logistics.core.entity.common.Country;
import com.globaltrade.logistics.core.entity.company.Company;
import com.globaltrade.logistics.core.entity.customer.Customer;
import com.globaltrade.logistics.core.entity.order.Order;
import com.globaltrade.logistics.core.entity.order.OrderStatus;
import com.globaltrade.logistics.core.entity.payment.PayHereDTO;
import com.globaltrade.logistics.core.service.ConfigService;
import com.globaltrade.logistics.ejb.repository.OrderRepository;
import com.globaltrade.logistics.ejb.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckoutServiceBeanTest {
    @InjectMocks
    private CheckoutServiceBean checkoutService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ConfigService configService;

    @Mock
    private Order order;

    @Mock
    private Customer customer;
    @Mock
    private Company company;
    @Mock
    private Country country;

    @Mock
    private Address address;

    @Test
    void processCheckout_shouldCreatePaymentDetailsSuccessfully() {

        UUID orderId = UUID.randomUUID();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(order.getOrderStatus()).thenReturn(OrderStatus.PENDING);
        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
        when(order.getId()).thenReturn(orderId);

        when(order.getCustomer()).thenReturn(customer);
        when(order.getTotalAmount()).thenReturn(new BigDecimal("2500.00"));
        when(customer.getFirstName()).thenReturn("Test name");

        when(customer.getLastName()).thenReturn("Test name");

        when(customer.getEmail()).thenReturn("test@example.com");
        when(customer.getMobile1()).thenReturn("0771234567");

        when(customer.getCompany()).thenReturn(company);
        when(company.getAddress()).thenReturn(address);
        when(address.getCountry()).thenReturn(country);

        when(country.getName()).thenReturn("Sri Lanka");

        when(configService.get("app.public.url")).thenReturn("https://test.ngrok.app");
        when(order.getItems()).thenReturn(Collections.emptyList());

        PayHereDTO result = checkoutService.processCheckout(orderId);

        assertNotNull(result);

        assertEquals(orderId.toString(),
                result.getOrder_id());

        assertEquals("2500.00", result.getAmount());

        assertEquals(
                "https://test.ngrok.app/api/payments/return",
                result.getReturn_url()
        );

        assertEquals(
                "https://test.ngrok.app/api/payments/cancel",
                result.getCancel_url()
        );

        assertEquals(
                "https://test.ngrok.app/api/payments/notify",
                result.getNotify_url()
        );

        assertEquals(
                "Test name",
                result.getFirst_name()
        );

        assertEquals(
                "Test name",
                result.getLast_name()
        );

        assertEquals(
                "test@example.com",
                result.getEmail()
        );


        verify(orderRepository).findById(orderId);
        verify(paymentRepository).findByOrderId(orderId);
        verify(configService, times(3)).get("app.public.url");
    }
}
