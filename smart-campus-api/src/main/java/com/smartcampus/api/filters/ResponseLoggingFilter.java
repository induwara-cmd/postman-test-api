package com.smartcampus.api.filters;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public final class ResponseLoggingFilter implements ContainerResponseFilter {
    private static final Logger logger = Logger.getLogger(ResponseLoggingFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        String method = requestContext.getMethod();
        String uri = requestContext.getUriInfo().getRequestUri().toString();
        int status = responseContext.getStatus();
        logger.log(Level.INFO, "{0} {1} -> {2}", new Object[] { method, uri, status });
    }
}
