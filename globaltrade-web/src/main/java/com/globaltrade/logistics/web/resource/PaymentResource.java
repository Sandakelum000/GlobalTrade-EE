package com.globaltrade.logistics.web.resource;

import com.globaltrade.logistics.core.dto.payment.PaymentRegistrationRequest;
import com.globaltrade.logistics.core.dto.payment.PaymentRegistrationResponse;
import com.globaltrade.logistics.core.service.CheckoutMessageService;
import com.globaltrade.logistics.core.service.PaymentService;
import com.globaltrade.logistics.core.util.PayHereUtil;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.UUID;
import java.util.logging.Logger;

@Path("/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentResource {

    private static final Logger LOGGER = Logger.getLogger(PaymentResource.class.getName());
    @Inject
    private CheckoutMessageService checkoutMessageService;

    @Path("/return")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response paymentSuccess(@QueryParam("orderId") String orderId) {
        return Response.seeOther(URI.create("http://localhost:8080/globaltrade/logistics" + "/invoice.html?orderId=" + orderId)).build();
    }

    @Path("/cancel")
    @GET
    public Response paymentCancel() {
        System.out.println("Payment canceled");
        return Response.ok().build();
    }


    @Path("/notify")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response paymentNotify(
            @FormParam("merchant_id") String merchantId,
            @FormParam("order_id") String orderId,
            @FormParam("payhere_amount") String payHereAmount,
            @FormParam("payhere_currency") String payHereCurrency,
            @FormParam("status_code") String statusCode,
            @FormParam("md5sig") String md5Sig
    ) {

        System.out.println("notify calling");

        System.out.println("orderId = " + orderId);
        System.out.println("statusCode = " + statusCode);

        if (orderId == null || statusCode == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Missing Form Parameters").build();
        }

        MultivaluedMap<String, String> formMap = new MultivaluedHashMap<>();
        formMap.add("merchant_id", merchantId);
        formMap.add("order_id", orderId);
        formMap.add("payhere_amount", payHereAmount);
        formMap.add("payhere_currency", payHereCurrency);
        formMap.add("status_code", statusCode);
        formMap.add("md5sig", md5Sig);

        if (!PayHereUtil.validateNotify(formMap)) {
            LOGGER.warning("PayHere notification signature validation failed");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("INVALID SIGNATURE").build();
        }

        System.out.println("SENDING JMS: " + orderId + " status=" + statusCode);

        PaymentRegistrationRequest payload = new PaymentRegistrationRequest(UUID.fromString(orderId), Integer.parseInt(statusCode));
        checkoutMessageService.sendPayload(payload);

        LOGGER.info("SENDING JMS PAYMENT: " + orderId + " status=" + statusCode);


        return Response.ok().build();
    }
}
