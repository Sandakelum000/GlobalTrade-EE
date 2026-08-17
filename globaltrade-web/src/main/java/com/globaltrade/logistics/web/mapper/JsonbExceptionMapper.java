package com.globaltrade.logistics.web.mapper;

import com.globaltrade.logistics.web.dto.error.ValidationErrorResponse;
import jakarta.json.bind.JsonbException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Provider
public class JsonbExceptionMapper
        implements ExceptionMapper<ProcessingException> {

    @Override
    public Response toResponse(ProcessingException exception) {

        Throwable cause = exception.getCause();

        if (cause instanceof JsonbException
                && cause.getMessage() != null
                && cause.getMessage().contains("No enum constant")) {

            String message = cause.getMessage();

            // Extract:
            // CustomerType.LOCA
            Pattern pattern = Pattern.compile(
                    "No enum constant ([\\w.]+)\\.([A-Z0-9_]+)"
            );

            Matcher matcher = pattern.matcher(message);

            if (matcher.find()) {

                String enumClassName = matcher.group(1);
                String invalidValue = matcher.group(2);

                if (enumClassName.endsWith("CustomerType")) {

                    ValidationErrorResponse response =
                            ValidationErrorResponse.of(
                                    "Validation failed",
                                    "customerType",
                                    "Invalid value '" + invalidValue
                                            + "'. Must be one of: "
                                            + Arrays.toString(
                                            com.globaltrade.logistics.core.entity.customer.CustomerType.values()
                                    ),
                                    Response.Status.BAD_REQUEST.getStatusCode()
                            );

                    return Response.status(Response.Status.BAD_REQUEST)
                            .type(MediaType.APPLICATION_JSON)
                            .entity(response)
                            .build();
                }
            }
        }

        // Anything else that is a ProcessingException
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(
                        ValidationErrorResponse.of(
                                "Invalid request",
                                "unknown",
                                "Invalid request body",
                                Response.Status.BAD_REQUEST.getStatusCode()
                        )
                )
                .build();
    }
}