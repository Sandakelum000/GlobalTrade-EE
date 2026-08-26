package com.globaltrade.logistics.web.resource;

import com.globaltrade.logistics.core.dto.customer.CustomerRegistrationRequest;
import com.globaltrade.logistics.core.dto.customer.CustomerRegistrationResponse;
import com.globaltrade.logistics.core.dto.customer.dashboard.CustomerDashboardResponse;
import com.globaltrade.logistics.core.dto.inventory.dashboard.CustomerInventoryResponse;
import com.globaltrade.logistics.core.service.CustomerDashboardService;
import com.globaltrade.logistics.core.service.CustomerInventoryService;
import com.globaltrade.logistics.core.service.CustomerService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;
import java.util.UUID;

@Path("/customer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    @EJB
    private CustomerService customerService;
    @EJB
    private CustomerDashboardService customerDashboardService;
    @EJB
    private CustomerInventoryService customerInventoryService;
    @Context
    private SecurityContext securityContext;

    @Path("/register")
    @POST
    public Response registerCustomer(@Valid @NotNull CustomerRegistrationRequest request){
        CustomerRegistrationResponse response = customerService.registerCustomer(request);
        return Response.status(Response.Status.CREATED)
                .entity(response)
                .build();
    }

    @Path("/dashboard")
    @GET
    @RolesAllowed("CUSTOMER")
    public Response getDashboard(){
        String username = securityContext.getUserPrincipal().getName();
        UUID customerId = customerService.findCustomerIdByUsername(username);

        CustomerDashboardResponse response = customerDashboardService.getDashboard(customerId);
        return Response.ok(response).build();

    }

    @Path("/inventory")
    @GET
    @RolesAllowed("CUSTOMER")
    public Response getInventory(){
        List<CustomerInventoryResponse> response = customerInventoryService.getAvailableInventory();
        return Response.ok(response).build();
    }

}
