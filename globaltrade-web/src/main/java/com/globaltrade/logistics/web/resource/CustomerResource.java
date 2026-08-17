package com.globaltrade.logistics.web.resource;

import com.globaltrade.logistics.core.dto.customer.CustomerRegistrationRequest;
import com.globaltrade.logistics.core.dto.customer.CustomerRegistrationResponse;
import com.globaltrade.logistics.core.service.CustomerService;
import jakarta.ejb.EJB;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/customer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    @EJB
    private CustomerService customerService;

    @Path("/register")
    @POST
    public Response registerCustomer(@Valid @NotNull CustomerRegistrationRequest request){
        CustomerRegistrationResponse response = customerService.registerCustomer(request);
        return Response.status(Response.Status.CREATED)
                .entity(response)
                .build();
    }
}
