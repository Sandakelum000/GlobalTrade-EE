package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.annotation.Audited;
import com.globaltrade.logistics.core.dto.order.OrderItemRequest;
import com.globaltrade.logistics.core.dto.order.OrderItemResponse;
import com.globaltrade.logistics.core.dto.order.OrderRegistrationRequest;
import com.globaltrade.logistics.core.dto.order.OrderRegistrationResponse;
import com.globaltrade.logistics.core.entity.audit.AuditAction;
import com.globaltrade.logistics.core.entity.customer.Customer;
import com.globaltrade.logistics.core.entity.order.Order;
import com.globaltrade.logistics.core.entity.order.OrderItem;
import com.globaltrade.logistics.core.entity.order.OrderStatus;
import com.globaltrade.logistics.core.entity.warehouse.Inventory;
import com.globaltrade.logistics.core.service.NumberSequenceService;
import com.globaltrade.logistics.core.service.OrderService;
import com.globaltrade.logistics.ejb.repository.CustomerRepository;
import com.globaltrade.logistics.ejb.repository.InventoryRepository;
import com.globaltrade.logistics.ejb.repository.OrderRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Stateless
public class OrderServiceBean implements OrderService {
    private static final String SEQUENCE_KEY = "ORDER";
    private static final String PREFIX = "ORD";
    private static final int WIDTH = 3;

    @Inject
    private OrderRepository orderRepository;
    @Inject
    private InventoryRepository inventoryRepository;
    @Inject
    private CustomerRepository customerRepository;
    @Inject
    private NumberSequenceService numberSequenceService;

    @Override
    @RolesAllowed({"CUSTOMER", "ADMIN",})
    @Audited(
            action = AuditAction.CREATE,
            entity = "Order"
    )
    @Transactional(Transactional.TxType.REQUIRED)
    public OrderRegistrationResponse createOrder(OrderRegistrationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Order Request is null");
        }
        if(request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer number not found: " + request.customerId()));

        String nextOrderNumber = numberSequenceService.next(SEQUENCE_KEY, PREFIX, WIDTH);

        Order order = Order.builder()
                .orderNumber(nextOrderNumber)
                .customer(customer)
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.items()) {
            if(itemRequest.quantity() == null || itemRequest.quantity() <= 0) {
                throw new IllegalArgumentException("Order item quantity must be greater than zero");
            }
            Inventory inventory = inventoryRepository.findByIdForUpdate(itemRequest.inventoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Inventory not found: " + itemRequest.inventoryId()));

            int reqQuantity =  itemRequest.quantity();

            if(reqQuantity > inventory.getAvailableQuantity()) {
                throw new IllegalStateException("Insufficient quantity for product:"+inventory.getProduct().getProductNumber());
            }
            inventory.reserveStock(reqQuantity);

            BigDecimal unitPrice = inventory.getSellingPrice();
            BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(reqQuantity));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .inventory(inventory)
                    .quantity(reqQuantity)
                    .unitPrice(unitPrice)
                    .build();

            //order.getItems().add(orderItem);
            order.addItem(orderItem);
            totalAmount = totalAmount.add(itemTotal);

        }

        order.setTotalAmount(totalAmount);
        orderRepository.save(order);
        return toResponse(order);
    }

    @Override
    @RolesAllowed({"CUSTOMER", "ADMIN"})
    @Audited(
            action = AuditAction.CANCEL,
            entity = "Order"
    )
    @Transactional(Transactional.TxType.REQUIRED)
    public OrderRegistrationResponse cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order id not found: " + orderId));

        if(order.getOrderStatus() == OrderStatus.SHIPPED || order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Order has been cancelled");
        }
        for(OrderItem orderItem : order.getItems()) {
            Inventory inventory = inventoryRepository.findByIdForUpdate(orderItem.getInventory().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Inventory not found: " + orderItem.getInventory().getId()));

            inventory.releaseReservedStock(orderItem.getQuantity());
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        return toResponse(order);
    }

    private OrderRegistrationResponse toResponse(@NonNull Order order) {
        List<OrderItemResponse> orderItemResList = order.getItems().stream()
                .map(item -> {
                    Inventory inventory = item.getInventory();
                    return new OrderItemResponse(
                            item.getId(),
                            inventory.getProduct().getProductNumber(),
                            inventory.getProduct().getTitle(),
                            item.getQuantity(),
                            item.getUnitPrice(),
                            item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                    );
                }).toList();

        return new OrderRegistrationResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomer().getCustomerNumber(),
                order.getCustomer().getFirstName() +" "+order.getCustomer().getLastName(),
                order.getOrderStatus().name(),
                order.getOrderDate(),
                order.getTotalAmount(),
                orderItemResList
        );
    }

}
