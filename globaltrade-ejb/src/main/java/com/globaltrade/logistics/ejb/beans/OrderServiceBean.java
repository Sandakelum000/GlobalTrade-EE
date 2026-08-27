package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.annotation.Audited;
import com.globaltrade.logistics.core.dto.order.OrderItemRequest;
import com.globaltrade.logistics.core.dto.order.OrderItemResponse;
import com.globaltrade.logistics.core.dto.order.OrderRegistrationRequest;
import com.globaltrade.logistics.core.dto.order.OrderRegistrationResponse;
import com.globaltrade.logistics.core.entity.audit.AuditAction;
import com.globaltrade.logistics.core.entity.common.Address;
import com.globaltrade.logistics.core.entity.customer.Customer;
import com.globaltrade.logistics.core.entity.order.Order;
import com.globaltrade.logistics.core.entity.order.OrderItem;
import com.globaltrade.logistics.core.entity.order.OrderStatus;
import com.globaltrade.logistics.core.entity.order.shipment.Shipment;
import com.globaltrade.logistics.core.entity.order.shipment.ShipmentItem;
import com.globaltrade.logistics.core.entity.order.shipment.ShipmentItemStatus;
import com.globaltrade.logistics.core.entity.order.shipment.ShipmentStatus;
import com.globaltrade.logistics.core.entity.warehouse.Inventory;
import com.globaltrade.logistics.core.exception.OrderCancellationException;
import com.globaltrade.logistics.core.exception.ResourceNotFoundException;
import com.globaltrade.logistics.core.service.NumberSequenceService;
import com.globaltrade.logistics.core.service.OrderService;
import com.globaltrade.logistics.ejb.repository.CustomerRepository;
import com.globaltrade.logistics.ejb.repository.InventoryRepository;
import com.globaltrade.logistics.ejb.repository.OrderRepository;
import com.globaltrade.logistics.ejb.repository.ShipmentRepository;
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
    @Inject
    private ShipmentRepository shipmentRepository;

    @Override
    @RolesAllowed({"CUSTOMER", "ADMIN",})
    @Audited(
            action = AuditAction.CREATE,
            entity = "Order"
    )
    @Transactional(Transactional.TxType.REQUIRED)
    public OrderRegistrationResponse createOrder(OrderRegistrationRequest request,UUID customerId) {
        if (request == null) {
            throw new IllegalArgumentException("Order Request is null");
        }
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("Order must have at least one item");
        }
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer number not found: " + customerId));

        String nextOrderNumber = numberSequenceService.next(SEQUENCE_KEY, PREFIX, WIDTH);

        Order order = Order.builder()
                .orderNumber(nextOrderNumber)
                .customer(customer)
                .shippingAddress(copyAddress(customer.getCompany().getAddress()))
                .orderDate(LocalDateTime.now())
                .orderStatus(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.items()) {
            if (itemRequest == null) {
                throw new IllegalArgumentException("Order item cannot be null");
            }
            if (itemRequest.inventoryId() == null) {
                throw new IllegalArgumentException("Inventory ID is required");
            }
            if (itemRequest.quantity() == null || itemRequest.quantity() <= 0) {
                throw new IllegalArgumentException("Order item quantity must be greater than zero");
            }

            Inventory inventory = inventoryRepository
                    .findByIdForUpdate(itemRequest.inventoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Inventory not found: " + itemRequest.inventoryId()));

            int reqQuantity = itemRequest.quantity();

            if (reqQuantity > inventory.getAvailableQuantity()) {
                throw new IllegalStateException("Insufficient quantity for product:" + inventory.getProduct().getProductNumber());
            }

            inventory.reserveStock(reqQuantity); // reserve stock

            BigDecimal unitPrice = inventory.getSellingPrice();
            if (unitPrice == null) {
                throw new IllegalStateException("Selling price is not configured for inventory: " + inventory.getInventoryNumber());
            }

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
                .orElseThrow(() -> new ResourceNotFoundException("Order id not found: " + orderId));

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new OrderCancellationException("Order is already cancelled");
        }

        if (order.getOrderStatus() == OrderStatus.SHIPPED ||
                order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new OrderCancellationException("Cannot cancel an order after shipped or delivered");
        }

        for (OrderItem orderItem : order.getItems()) {
            Inventory inventory = inventoryRepository
                    .findByIdForUpdate(orderItem.getInventory().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory not found: " + orderItem.getInventory().getId()));

            inventory.releaseReservedStock(orderItem.getQuantity());
        }

        List<Shipment> shipments = shipmentRepository.findByOrderId(orderId);
        for (Shipment shipment : shipments) {

            if(shipment.getStatus() == ShipmentStatus.PENDING ||
                    shipment.getStatus() == ShipmentStatus.PROCESSING) {
                shipment.setStatus(ShipmentStatus.CANCELLED);

                if(shipment.getItems() != null){
                    for (ShipmentItem shipmentItem :  shipment.getItems()) {
                        shipmentItem.setStatus(ShipmentItemStatus.CANCELLED);
                    }
                }
            }else{
                throw new OrderCancellationException("Shipment Status is already cancelled");
            }
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
                order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName(),
                order.getOrderStatus().name(),
                order.getOrderDate(),
                order.getTotalAmount(),
                orderItemResList
        );
    }

    private Address copyAddress(Address address) {
        if (address == null) {
            return null;
        }
        return Address.builder()
                .line1(address.getLine1())
                .line2(address.getLine2())
                .line3(address.getLine3())
                .city(address.getCity())
                .district(address.getDistrict())
                .stateProvince(address.getStateProvince())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .build();
    }

}
