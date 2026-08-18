package com.globaltrade.logistics.web.mapper;

import com.globaltrade.logistics.core.exception.ProductAlreadyExistsException;
import com.globaltrade.logistics.web.dto.error.ValidationErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.springframework.lang.NonNull;

@Provider
public class ProductAlreadyExistsExceptionMapper implements ExceptionMapper<ProductAlreadyExistsException> {
    @Override
    public Response toResponse(@NonNull ProductAlreadyExistsException exception) {
        ValidationErrorResponse response =
                ValidationErrorResponse.of(
                        "Product already exists",
                        "product title",
                        exception.getMessage(),
                        Response.Status.CONFLICT.getStatusCode()
                );

        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(response).build();
    }
}
