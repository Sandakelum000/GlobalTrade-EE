package com.globaltrade.logistics.web.mapper;

import com.globaltrade.logistics.core.exception.ShipmentShippingException;
import com.globaltrade.logistics.web.dto.error.ValidationErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ShipmentShippingExceptionMapper implements ExceptionMapper<ShipmentShippingException> {
    @Override
    public Response toResponse(ShipmentShippingException exception) {
        int statusCode = Response.Status.CONFLICT.getStatusCode();

        ValidationErrorResponse errorResponse = ValidationErrorResponse.of(
                "Shipment shipping failed ",
                "Shipment Status",
                exception.getMessage(),
                statusCode
        );
        return Response.status(statusCode)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(errorResponse)
                .build();
    }
}
