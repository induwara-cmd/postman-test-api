package com.smartcampus.api;

import com.smartcampus.api.filters.RequestLoggingFilter;
import com.smartcampus.api.filters.ResponseLoggingFilter;
import com.smartcampus.api.mappers.ApiExceptionMapper;
import com.smartcampus.api.mappers.GlobalExceptionMapper;
import com.smartcampus.api.mappers.NotFoundExceptionMapper;
import com.smartcampus.api.resources.DiscoveryResource;
import com.smartcampus.api.resources.RoomsResource;
import com.smartcampus.api.resources.SensorsResource;
import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api/v1")
public final class SmartCampusApplication extends ResourceConfig {
    public SmartCampusApplication() {
        register(JacksonFeature.class);

        register(DiscoveryResource.class);
        register(RoomsResource.class);
        register(SensorsResource.class);

        register(ApiExceptionMapper.class);
        register(NotFoundExceptionMapper.class);
        register(GlobalExceptionMapper.class);

        register(RequestLoggingFilter.class);
        register(ResponseLoggingFilter.class);
    }
}
