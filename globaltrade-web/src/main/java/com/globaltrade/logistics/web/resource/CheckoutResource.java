package com.globaltrade.logistics.web.resource;

import com.globaltrade.logistics.core.entity.payment.PayHereDTO;
import com.globaltrade.logistics.core.service.CheckoutService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/checkouts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CheckoutResource {
    @EJB
    private CheckoutService checkoutService;

    public record CheckoutRequest(UUID orderId) { }

    @Path("/customer-checkouts")
    @POST
    public Response customerCheckout(CheckoutRequest request){
        PayHereDTO payHereDTO = checkoutService.processCheckout(request.orderId);
        return Response.ok(payHereDTO).build();
    }

}
