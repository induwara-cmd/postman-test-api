package com.smartcampus.api.mappers;

import com.smartcampus.api.errors.ApiErrorResponse;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public final class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    @Override
    public Response toResponse(NotFoundException exception) {
        ApiErrorResponse payload = new ApiErrorResponse(404, "Not Found", "Resource not found",
                System.currentTimeMillis());
        return Response.status(404).type(MediaType.APPLICATION_JSON).entity(payload).build();
    }
}
