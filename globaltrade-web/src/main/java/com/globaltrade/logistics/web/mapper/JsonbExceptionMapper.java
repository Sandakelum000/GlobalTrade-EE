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

    private static final Pattern ENUM_PATTERN =
            Pattern.compile("No enum constant ([\\w.$]+)\\.([A-Z0-9_]+)");

    @Override
    public Response toResponse(ProcessingException exception) {

        Throwable cause = exception.getCause();
        if (cause instanceof JsonbException && cause.getMessage() != null) {

            String message = cause.getMessage();
            Matcher matcher = ENUM_PATTERN.matcher(message);

            if (matcher.find()) {
                String enumClassName = matcher.group(1);
                String invalidValue = matcher.group(2);

                String field = extractFieldName(exception);
                String allowedValues = getAllowedEnumValues(enumClassName);

                String detail;

                if (allowedValues != null) {
                    detail = "Invalid value '" + invalidValue + "'. Must be one of: " + allowedValues;
                } else {
                    detail = "Invalid value '" + invalidValue + "'";
                }
                ValidationErrorResponse response =
                        ValidationErrorResponse.of(
                                "Validation failed",
                                field,
                                detail,
                                Response.Status.BAD_REQUEST.getStatusCode()
                        );

                return Response.status(Response.Status.BAD_REQUEST)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(response)
                        .build();
            }
        }

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

    private String getAllowedEnumValues(String enumClassName) {
        try {
            Class<?> enumClass = Class.forName(enumClassName);

            if (!enumClass.isEnum()) {
                return null;
            }
            Object[] constants = enumClass.getEnumConstants();
            return Arrays.toString(constants);

        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private String extractFieldName(ProcessingException exception) {

        Throwable cause = exception.getCause();
        if (cause == null || cause.getMessage() == null) {
            return "unknown";
        }

        String message = cause.getMessage();
        Matcher matcher = Pattern.compile("property ['\"]?([\\w]+)['\"]?").matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "unknown";
    }
}