package com.globaltrade.logistics.web.resource;

import com.globaltrade.logistics.core.dto.order.OrderRegistrationRequest;
import com.globaltrade.logistics.core.dto.order.OrderRegistrationResponse;
import com.globaltrade.logistics.core.service.CustomerService;
import com.globaltrade.logistics.core.service.OrderService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.UUID;

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    @Inject
    private OrderService orderService;
    @Inject
    private CustomerService customerService;
    @Context
    private SecurityContext securityContext;

    @POST
    @Path("/create")
    @RolesAllowed({"CUSTOMER","ADMIN"})
    public Response createOrder(@NotNull @Valid OrderRegistrationRequest request) {
        String username = securityContext.getUserPrincipal().getName();
        UUID customerId = customerService.findCustomerIdByUsername(username);

        OrderRegistrationResponse response = orderService.createOrder(request,customerId);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @POST
    @Path("/calcel/{orderId}")
    @RolesAllowed({"CUSTOMER","ADMIN"})
    public Response cancelOrder(@PathParam("orderId") UUID orderId) {
        OrderRegistrationResponse response = orderService.cancelOrder(orderId);
        return Response.status(Response.Status.OK).entity(response).build();
    }
}
