package com.globaltrade.logistics.core.service;

import com.globaltrade.logistics.core.dto.admin.order.AdminOrderDetailsResponse;
import com.globaltrade.logistics.core.dto.admin.order.AdminOrderListResponse;
import com.globaltrade.logistics.core.dto.admin.order.PageResponse;
import com.globaltrade.logistics.core.entity.order.OrderStatus;
import jakarta.ejb.Local;

import java.util.UUID;

@Local
public interface AdminOrderService {
    PageResponse<AdminOrderListResponse> getOrders(
            String search,
            OrderStatus status,
            String sortBy,
            String direction,
            int page,
            int size
    );
    AdminOrderDetailsResponse getOrderDetails(UUID orderId);
    void cancelExpiredUnpaidOrders();
}
