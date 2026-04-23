package com.smartcampus.api.resources;

import com.smartcampus.api.errors.ForbiddenException;
import com.smartcampus.api.errors.NotFoundApiException;
import com.smartcampus.api.models.Sensor;
import com.smartcampus.api.models.SensorReading;
import com.smartcampus.api.store.CampusStore;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class SensorReadingsResource {
    private final String sensorId;
    private final CampusStore store;

    public SensorReadingsResource(String sensorId, CampusStore store) {
        this.sensorId = sensorId;
        this.store = store;
    }

    @GET
    public List<SensorReading> listReadings() {
        store.getSensor(sensorId).orElseThrow(() -> new NotFoundApiException("Sensor not found"));
        return store.listReadings(sensorId);
    }

    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = store.getSensorInternal(sensorId)
                .orElseThrow(() -> new NotFoundApiException("Sensor not found"));
        if (CampusStore.isMaintenance(sensor)) {
            throw new ForbiddenException("Sensor is in MAINTENANCE");
        }

        SensorReading created = store.addReading(sensorId, reading == null ? new SensorReading() : reading);
        return Response.status(201).entity(created).build();
    }
}
