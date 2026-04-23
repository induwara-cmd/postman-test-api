package com.smartcampus.api.filters;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public final class RequestLoggingFilter implements ContainerRequestFilter {
    private static final Logger logger = Logger.getLogger(RequestLoggingFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String method = requestContext.getMethod();
        String uri = requestContext.getUriInfo().getRequestUri().toString();
        logger.log(Level.INFO, "{0} {1}", new Object[] { method, uri });
    }
}
