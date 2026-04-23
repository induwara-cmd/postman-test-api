package com.smartcampus.api.mappers;

import com.smartcampus.api.errors.ApiErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public final class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger logger = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {
        logger.log(Level.SEVERE, "Unhandled exception", exception);
        ApiErrorResponse payload = new ApiErrorResponse(500, "Internal Server Error", "Internal server error",
                System.currentTimeMillis());
        return Response.status(500).type(MediaType.APPLICATION_JSON).entity(payload).build();
    }
}
