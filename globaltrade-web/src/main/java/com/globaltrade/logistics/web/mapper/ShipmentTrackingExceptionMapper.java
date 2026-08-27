package com.globaltrade.logistics.web.mapper;

import com.globaltrade.logistics.core.exception.ShipmentTrackingException;
import com.globaltrade.logistics.web.dto.error.ValidationErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ShipmentTrackingExceptionMapper implements ExceptionMapper<ShipmentTrackingException> {
    @Override
    public Response toResponse(ShipmentTrackingException exception) {
        int statusCode = Response.Status.CONFLICT.getStatusCode();

        ValidationErrorResponse errorResponse = ValidationErrorResponse.of(
                "Shipment Tracking update failed",
                "Shipment Tracking Status",
                exception.getMessage(),
                statusCode
        );

        return Response.status(statusCode)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorResponse)
                .build();
    }
}
