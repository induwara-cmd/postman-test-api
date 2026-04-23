package com.smartcampus.api.resources;

import com.smartcampus.api.resources.dto.DiscoveryDto;
import com.smartcampus.api.resources.dto.LinkDto;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import java.util.List;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public final class DiscoveryResource {
    @GET
    public DiscoveryDto discovery(@Context UriInfo uriInfo) {
        String base = uriInfo.getBaseUri().toString();
        if (!base.endsWith("/")) {
            base = base + "/";
        }

        List<LinkDto> links = List.of(
                new LinkDto("self", base, "GET"),
                new LinkDto("rooms", base + "rooms", "GET"),
                new LinkDto("create-room", base + "rooms", "POST"),
                new LinkDto("sensors", base + "sensors", "GET"),
                new LinkDto("create-sensor", base + "sensors", "POST"));

        return new DiscoveryDto("Smart Campus API", "v1", links);
    }
}
