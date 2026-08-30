package com.globaltrade.logistics.web.resource;

import com.globaltrade.logistics.core.dto.admin.dashboard.AdminDashboardResponse;
import com.globaltrade.logistics.core.dto.admin.inventory.AdminInventoryDetailsResponse;
import com.globaltrade.logistics.core.dto.admin.inventory.AdminInventoryListResponse;
import com.globaltrade.logistics.core.dto.admin.order.AdminOrderListResponse;
import com.globaltrade.logistics.core.dto.admin.order.PageResponse;
import com.globaltrade.logistics.core.dto.admin.shipment.AdminShipmentListResponse;
import com.globaltrade.logistics.core.entity.order.OrderStatus;
import com.globaltrade.logistics.core.entity.order.shipment.ShipmentStatus;
import com.globaltrade.logistics.core.service.AdminDashboardService;
import com.globaltrade.logistics.core.service.AdminInventoryService;
import com.globaltrade.logistics.core.service.AdminOrderService;
import com.globaltrade.logistics.core.service.AdminShipmentService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
public class AdminResource {

    @EJB
    private AdminDashboardService adminDashboardService;
    @EJB
    private AdminOrderService adminOrderService;
    @EJB
    private AdminShipmentService adminShipmentService;
    @EJB
    private AdminInventoryService adminInventoryService;


    @GET
    @Path("/dashboard")
    public Response getDashboard() {
        AdminDashboardResponse response = adminDashboardService.getDashboard();
        return Response.ok(response).build();
    }

    @GET
    @Path("/orders")
    public Response getOrder(
            @QueryParam("search") String search,
            @QueryParam("status") OrderStatus status,
            @QueryParam("sortBy")
            @DefaultValue("createdAt") String sortBy,
            @QueryParam("direction")
            @DefaultValue("DESC") String direction,
            @QueryParam("page")
            @DefaultValue("0") int page,
            @QueryParam("size")
            @DefaultValue("20") int size
    ) {
        PageResponse<AdminOrderListResponse> response = adminOrderService.getOrders(search, status, sortBy, direction, page, size);
        return Response.ok(response).build();
    }

    @GET
    @Path("orders/{orderId}")
    public Response getOrderDetails(@PathParam("orderId") UUID orderId) {
        return Response.ok(adminOrderService.getOrderDetails(orderId)).build();
    }

    @GET
    @Path("/shipments")
    @RolesAllowed({"ADMIN","OPERATIONS_MANAGER"})
    public Response getShipments(
            @QueryParam("search")
            String search,
            @QueryParam("status")
            ShipmentStatus status,
            @QueryParam("warehouseId")
            UUID warehouseId,
            @QueryParam("sortBy")
            @DefaultValue("createdAt")
            String sortBy,
            @QueryParam("direction")
            @DefaultValue("DESC")
            String direction,
            @QueryParam("page")
            @DefaultValue("0")
            int page,
            @QueryParam("size")
            @DefaultValue("20")
            int size
    ){
        PageResponse<AdminShipmentListResponse> response =
                adminShipmentService.getShipments(search, status, warehouseId, sortBy, direction, page, size);
        return Response.ok(response).build();
    }

    @GET
    @Path("shipments/{shipmentId}")
    @RolesAllowed({"ADMIN","OPERATIONS_MANAGER"})
    public Response getShipmentDetails(@PathParam("shipmentId") UUID shipmentId) {
        return Response.ok(adminShipmentService.getShipmentDetails(shipmentId)).build();
    }

    @GET
    @Path("/inventory")
    public Response getInventories(
            @QueryParam("search")
            String search,
            @QueryParam("warehouseId")
            UUID warehouseId,
            @QueryParam("productId")
            UUID productId,
            @QueryParam("lowStock")
            Boolean lowStock,
            @QueryParam("outOfStock")
            Boolean outOfStock,
            @QueryParam("sortBy")
            @DefaultValue("updatedAt")
            String sortBy,
            @QueryParam("direction")
            @DefaultValue("DESC")
            String direction,
            @QueryParam("page")
            @DefaultValue("0")
            int page,
            @QueryParam("size")
            @DefaultValue("20")
            int size) {
        PageResponse<AdminInventoryListResponse> response = adminInventoryService.getInventories(search, warehouseId, productId,
                lowStock, outOfStock, sortBy, direction, page, size);

        return Response.ok(response).build();
    }

    @GET
    @Path("/inventory/{inventoryId}")
    public Response getInventoryDetails(@PathParam("inventoryId") UUID inventoryId) {
        AdminInventoryDetailsResponse response = adminInventoryService.getInventoryDetails(inventoryId);
        return Response.ok(response).build();
    }
}
