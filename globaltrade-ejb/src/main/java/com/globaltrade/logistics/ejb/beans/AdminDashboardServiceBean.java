package com.globaltrade.logistics.ejb.beans;

import com.globaltrade.logistics.core.dto.admin.dashboard.*;
import com.globaltrade.logistics.core.dto.customer.dashboard.MonthlyOrderResponse;
import com.globaltrade.logistics.core.dto.customer.dashboard.OrderStatusCountResponse;
import com.globaltrade.logistics.core.dto.customer.dashboard.RecentOrderResponse;
import com.globaltrade.logistics.core.dto.customer.dashboard.ShipmentStatusCountResponse;
import com.globaltrade.logistics.core.entity.order.OrderStatus;
import com.globaltrade.logistics.core.entity.order.shipment.ShipmentStatus;
import com.globaltrade.logistics.core.service.AdminDashboardService;
import com.globaltrade.logistics.ejb.repository.*;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;

import java.util.List;

@Stateless
public class AdminDashboardServiceBean implements AdminDashboardService {
    @Inject
    private OrderRepository orderRepository;
    @Inject
    private ShipmentRepository shipmentRepository;
    @Inject
    private CustomerRepository customerRepository;
    @Inject
    private VendorRepository vendorRepository;
    @Inject
    private ProductRepository productRepository;
    @Inject
    private WarehouseRepository warehouseRepository;
    @Inject
    private InventoryRepository inventoryRepository;
    @Inject
    private PaymentRepository paymentRepository;

    @Override
    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    @RolesAllowed({"ADMIN"})
    public AdminDashboardResponse getDashboard() {
        AdminDashboardSummaryResponse summary =
                new AdminDashboardSummaryResponse(
                        orderRepository.countAll(),
                        orderRepository.countByStatus(OrderStatus.PENDING),
                        orderRepository.countByStatus(OrderStatus.CONFIRMED),
                        orderRepository.countByStatus(OrderStatus.CANCELLED),
                        orderRepository.countByStatus(OrderStatus.DELIVERED),

                        shipmentRepository.countActiveShipments(),
                        shipmentRepository.countByStatus(ShipmentStatus.DELIVERED),

                        customerRepository.countAll(),
                        vendorRepository.countAll(),
                        productRepository.countAll(),
                        warehouseRepository.countAll(),

                        inventoryRepository.countLowStock(),

                        paymentRepository.calculateTotalRevenue()
                );

        List<OrderStatusCountResponse> orderStatus = orderRepository.countOrdersByStatus();

        List<ShipmentStatusCountResponse> shipmentStatus = shipmentRepository.countShipmentsByStatus();

        List<MonthlyOrderResponse> monthlyOrders = orderRepository.countMonthlyOrders();

        List<MonthlyRevenueResponse> monthlyRevenue = paymentRepository.calculateMonthlyRevenue();

        List<RecentOrderResponse> recentOrders = orderRepository.findRecentOrders(5);

        List<AdminShipmentResponse> activeShipments = shipmentRepository.findActiveShipmentRecords(10);

        List<LowStockResponse> lowStockItems = inventoryRepository.findLowStockRecords(10);

        return new AdminDashboardResponse(
                summary,
                orderStatus,
                shipmentStatus,
                monthlyOrders,
                monthlyRevenue,
                recentOrders,
                activeShipments,
                lowStockItems
        );
    }
}
