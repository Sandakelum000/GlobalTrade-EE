package com.globaltrade.logistics.web.resource;

import com.globaltrade.logistics.core.dto.product.ProductRegistrationRequest;
import com.globaltrade.logistics.core.dto.product.ProductRegistrationResponse;
import com.globaltrade.logistics.core.service.ProductService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/product")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN"})
public class ProductResource {

    @EJB
    private ProductService productService;

    @Path("/register")
    @POST
    public Response registerProduct(@NotNull @Valid ProductRegistrationRequest request) {
        ProductRegistrationResponse response = productService.registerProduct(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

}
