package com.globaltrade.logistics.web.mapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.globaltrade.logistics.core.exception.PaymentException;
import com.globaltrade.logistics.web.dto.error.ValidationErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;


@Provider
public class PaymentExceptionMapper implements ExceptionMapper<PaymentException> {

    @Override
    public Response toResponse(PaymentException exception) {
        int statusCode = Response.Status.CONFLICT.getStatusCode();
        ValidationErrorResponse errorResponse = ValidationErrorResponse.of("" +
                        "Payment has failed",
                "Payment",
                exception.getMessage(),
                statusCode);

        return Response.status(statusCode).type(MediaType.APPLICATION_JSON).entity(errorResponse).build();
    }
}
