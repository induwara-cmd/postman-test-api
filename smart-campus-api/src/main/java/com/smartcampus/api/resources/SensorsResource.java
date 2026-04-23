package com.smartcampus.api.resources;

import com.smartcampus.api.errors.ForbiddenException;
import com.smartcampus.api.errors.NotFoundApiException;
import com.smartcampus.api.errors.UnprocessableEntityException;
import com.smartcampus.api.models.Sensor;
import com.smartcampus.api.store.CampusStore;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class SensorsResource {
    private final CampusStore store = CampusStore.getInstance();

    @GET
    public List<Sensor> listSensors(@QueryParam("type") String type) {
        return store.listSensors(type);
    }

    @POST
    public Response createSensor(Sensor sensor, @Context UriInfo uriInfo) {
        if (sensor == null || sensor.getRoomId() == null || sensor.getRoomId().isBlank()) {
            throw new UnprocessableEntityException("roomId is required");
        }
        if (!store.roomExists(sensor.getRoomId())) {
            throw new UnprocessableEntityException("roomId does not exist");
        }

        Sensor created = store.createSensor(sensor);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getId()).build();
        return Response.created(location).entity(created).build();
    }

    @GET
    @Path("{sensorId}")
    public Sensor getSensor(@PathParam("sensorId") String sensorId) {
        return store.getSensor(sensorId).orElseThrow(() -> new NotFoundApiException("Sensor not found"));
    }

    @PUT
    @Path("{sensorId}")
    public Sensor updateSensor(@PathParam("sensorId") String sensorId, Sensor sensor) {
        Sensor existing = store.getSensorInternal(sensorId)
                .orElseThrow(() -> new NotFoundApiException("Sensor not found"));
        if (CampusStore.isMaintenance(existing)) {
            throw new ForbiddenException("Sensor is in MAINTENANCE");
        }

        if (sensor == null || sensor.getRoomId() == null || sensor.getRoomId().isBlank()) {
            throw new UnprocessableEntityException("roomId is required");
        }
        if (!store.roomExists(sensor.getRoomId())) {
            throw new UnprocessableEntityException("roomId does not exist");
        }

        return store
                .updateSensor(sensorId, sensor)
                .orElseThrow(() -> new NotFoundApiException("Sensor not found"));
    }

    @DELETE
    @Path("{sensorId}")
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {
        Sensor existing = store.getSensorInternal(sensorId)
                .orElseThrow(() -> new NotFoundApiException("Sensor not found"));
        if (CampusStore.isMaintenance(existing)) {
            throw new ForbiddenException("Sensor is in MAINTENANCE");
        }

        store.deleteSensor(sensorId);
        return Response.noContent().build();
    }

    @Path("{sensorId}/read")
    public SensorReadingsResource readings(@PathParam("sensorId") String sensorId) {
        return new SensorReadingsResource(sensorId, store);
    }
}
