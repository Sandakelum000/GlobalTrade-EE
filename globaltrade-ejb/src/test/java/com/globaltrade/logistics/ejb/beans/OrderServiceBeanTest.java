package com.globaltrade.logistics.ejb.beans;


import com.globaltrade.logistics.core.dto.order.OrderItemRequest;
import com.globaltrade.logistics.core.dto.order.OrderRegistrationRequest;
import com.globaltrade.logistics.core.dto.order.OrderRegistrationResponse;
import com.globaltrade.logistics.core.entity.common.Address;
import com.globaltrade.logistics.core.entity.common.Country;
import com.globaltrade.logistics.core.entity.company.Company;
import com.globaltrade.logistics.core.entity.customer.Customer;
import com.globaltrade.logistics.core.entity.order.Order;
import com.globaltrade.logistics.core.entity.product.Product;
import com.globaltrade.logistics.core.entity.warehouse.Inventory;
import com.globaltrade.logistics.core.exception.OrderCreationException;
import com.globaltrade.logistics.core.service.NumberSequenceService;
import com.globaltrade.logistics.ejb.repository.CustomerRepository;
import com.globaltrade.logistics.ejb.repository.InventoryRepository;
import com.globaltrade.logistics.ejb.repository.OrderRepository;
import com.globaltrade.logistics.ejb.repository.ShipmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceBeanTest {
    @InjectMocks
    private OrderServiceBean orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private NumberSequenceService numberSequenceService;
    @Mock
    private Customer customer;
    @Mock
    private Inventory inventory;
    @Mock
    private Company company;
    @Mock
    private Address address;

    @Test
     void createOrder_shouldCreateOrderSuccessfully() {

        UUID customerId = UUID.randomUUID();
        UUID inventoryId = UUID.randomUUID();

        OrderItemRequest itemRequest = new OrderItemRequest(inventoryId, 2);

        OrderRegistrationRequest request =
                new OrderRegistrationRequest(List.of(itemRequest));

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(customer.getCompany()).thenReturn(company);
        when(company.getAddress()).thenReturn(address);

        when(numberSequenceService.next("ORDER", "ORD", 3))
                .thenReturn("ORD001");

        when(inventoryRepository.findByIdForUpdate(inventoryId))
                .thenReturn(Optional.of(inventory));

        when(inventory.getAvailableQuantity())
                .thenReturn(10);

        when(inventory.getSellingPrice())
                .thenReturn(new BigDecimal("100.00"));

        Product product = mock(Product.class);
        when(inventory.getProduct()).thenReturn(product);


        OrderRegistrationResponse response =
                orderService.createOrder(request, customerId);

        assertNotNull(response);

        verify(customerRepository)
                .findById(customerId);

        verify(inventoryRepository)
                .findByIdForUpdate(inventoryId);

        verify(inventory)
                .reserveStock(2);

        verify(numberSequenceService)
                .next("ORDER", "ORD", 3);

        verify(orderRepository)
                .save(any(Order.class));
    }

    @Test
    void createOrder_shouldThrowException_whenRequestIsNull() {
        UUID customerId = UUID.randomUUID();
        assertThrows(
                OrderCreationException.class,
                () -> orderService.createOrder(null, customerId)
        );
        verifyNoInteractions(
                customerRepository,
                inventoryRepository,
                numberSequenceService,
                orderRepository
        );
    }

    @Test
    void createOrder_shouldThrowException_whenCustomerIdIsNull() {

        OrderRegistrationRequest request = mock(OrderRegistrationRequest.class);

        assertThrows(
                OrderCreationException.class,
                () -> orderService.createOrder(request, null)
        );

        verifyNoInteractions(
                customerRepository,
                inventoryRepository,
                numberSequenceService,
                orderRepository
        );
    }

    @Test
    void createOrder_shouldThrowException_whenItemsAreEmpty() {
        UUID customerId = UUID.randomUUID();

        OrderRegistrationRequest request =
                new OrderRegistrationRequest(List.of());

        assertThrows(
                OrderCreationException.class,
                () -> orderService.createOrder(request, customerId)
        );

        verifyNoInteractions(
                customerRepository,
                inventoryRepository,
                numberSequenceService,
                orderRepository
        );
    }
    @Test
    void createOrder_shouldThrowException_whenInsufficientStock() {

        UUID customerId = UUID.randomUUID();
        UUID inventoryId = UUID.randomUUID();

        OrderItemRequest itemRequest =
                new OrderItemRequest(
                        inventoryId,
                        20
                );

        OrderRegistrationRequest request =
                new OrderRegistrationRequest(
                        List.of(itemRequest)
                );

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(customer.getCompany()).thenReturn(company);
        when(company.getAddress()).thenReturn(address);

        when(inventoryRepository.findByIdForUpdate(inventoryId))
                .thenReturn(Optional.of(inventory));

        when(inventory.getAvailableQuantity())
                .thenReturn(10);

        Product product = mock(Product.class);

        when(inventory.getProduct())
                .thenReturn(product);

        when(product.getProductNumber())
                .thenReturn("PR-001");

        assertThrows(
                OrderCreationException.class,
                () -> orderService.createOrder(request, customerId)
        );

        verify(inventoryRepository)
                .findByIdForUpdate(inventoryId);

        verify(inventory, never())
                .reserveStock(anyInt());

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    void createOrder_shouldThrowException_whenInventoryDoesNotExist() {

        UUID customerId = UUID.randomUUID();
        UUID inventoryId = UUID.randomUUID();

        OrderItemRequest itemRequest =
                new OrderItemRequest(inventoryId, 2);

        OrderRegistrationRequest request =
                new OrderRegistrationRequest(
                        List.of(itemRequest)
                );

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        when(customer.getCompany()).thenReturn(company);
        when(company.getAddress()).thenReturn(address);

        when(inventoryRepository.findByIdForUpdate(inventoryId))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderCreationException.class,
                () -> orderService.createOrder(request, customerId)
        );

        verify(orderRepository, never())
                .save(any(Order.class));
    }
}
