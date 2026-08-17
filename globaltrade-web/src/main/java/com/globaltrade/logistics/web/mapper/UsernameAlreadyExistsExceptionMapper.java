package com.globaltrade.logistics.web.mapper;

import com.globaltrade.logistics.web.dto.error.ValidationErrorResponse;
import com.globaltrade.logistics.core.exception.UsernameAlreadyExistsException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UsernameAlreadyExistsExceptionMapper implements ExceptionMapper<UsernameAlreadyExistsException> {
    @Override
    public Response toResponse(UsernameAlreadyExistsException exception) {
        ValidationErrorResponse response =
                ValidationErrorResponse.of(
                        "Username already exists",
                        "username",
                        exception.getMessage(),
                        Response.Status.CONFLICT.getStatusCode()
                );

        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.APPLICATION_JSON)
                .entity(response).build();
    }
}
