package com.globaltrade.logistics.web.mapper;

import com.globaltrade.logistics.core.exception.OrderCancellationException;
import com.globaltrade.logistics.web.dto.error.ValidationErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class OrderCancellationExceptionMapper implements ExceptionMapper<OrderCancellationException> {
    @Override
    public Response toResponse(OrderCancellationException exception) {
        int statusCode = Response.Status.CONFLICT.getStatusCode();

        ValidationErrorResponse response = ValidationErrorResponse.of(
                "Order cancellation failed",
                "order status",
                exception.getMessage(),
                statusCode
        );

        return Response.status(statusCode)
                .type(MediaType.APPLICATION_JSON)
                .entity(response)
                .entity(response).build();
    }
}
