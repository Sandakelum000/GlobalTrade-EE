package com.globaltrade.logistics.web.resource;

import com.globaltrade.logistics.core.dto.payment.PaymentRegistrationRequest;
import com.globaltrade.logistics.core.dto.payment.PaymentRegistrationResponse;
import com.globaltrade.logistics.core.service.PaymentService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentResource {

    @Inject
    private PaymentService paymentService;

    @POST
    @RolesAllowed({"CUSTOMER","ADMIN"})
    public Response createPayment(@NotNull @Valid PaymentRegistrationRequest request) {
        PaymentRegistrationResponse response = paymentService.makePayment(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }
}
