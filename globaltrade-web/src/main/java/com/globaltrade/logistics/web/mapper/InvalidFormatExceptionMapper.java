package com.globaltrade.logistics.web.mapper;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.globaltrade.logistics.web.dto.error.ValidationErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Arrays;

@Provider
public class InvalidFormatExceptionMapper implements ExceptionMapper<InvalidFormatException> {

    @Override
    public Response toResponse(InvalidFormatException exception) {
        String field = exception.getPath().isEmpty() ? "unknown" : exception.getPath()
                .get(exception.getPath().size() - 1)
                .getFieldName();

        String message = "Invalid value";
        Class<?> targetType = exception.getTargetType();

        if (targetType != null && targetType.isEnum()) {
            Object[] constants = targetType.getEnumConstants();
            message = "Value must be one of: "
                    + Arrays.toString(constants);
        }
        int status = Response.Status.BAD_REQUEST.getStatusCode();

        ValidationErrorResponse response =
                ValidationErrorResponse.of("Validation failed",field, message, status);

        return Response.status(status).entity(response).build();

    }
}
