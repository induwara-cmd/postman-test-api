package com.smartcampus.api.mappers;

import com.smartcampus.api.errors.ApiErrorResponse;
import com.smartcampus.api.errors.ApiException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public final class ApiExceptionMapper implements ExceptionMapper<ApiException> {
    @Override
    public Response toResponse(ApiException exception) {
        Response.Status statusEnum = Response.Status.fromStatusCode(exception.getStatus());
        String error = statusEnum == null ? "Error" : statusEnum.getReasonPhrase();

        ApiErrorResponse payload = new ApiErrorResponse(
                exception.getStatus(), error, exception.getSafeMessage(), System.currentTimeMillis());

        return Response.status(exception.getStatus()).type(MediaType.APPLICATION_JSON).entity(payload).build();
    }
}
