package com.smartcampus.api.resources;

import com.smartcampus.api.errors.ConflictException;
import com.smartcampus.api.errors.NotFoundApiException;
import com.smartcampus.api.models.Room;
import com.smartcampus.api.store.CampusStore;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class RoomsResource {
    private final CampusStore store = CampusStore.getInstance();

    @GET
    public List<Room> listRooms() {
        return store.listRooms();
    }

    @POST
    public Response createRoom(Room room, @Context UriInfo uriInfo) {
        Room created = store.createRoom(room);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getId()).build();
        return Response.created(location).entity(created).build();
    }

    @GET
    @Path("{roomId}")
    public Room getRoom(@PathParam("roomId") String roomId) {
        return store.getRoom(roomId).orElseThrow(() -> new NotFoundApiException("Room not found"));
    }

    @PUT
    @Path("{roomId}")
    public Room updateRoom(@PathParam("roomId") String roomId, Room room) {
        return store
                .updateRoom(roomId, room)
                .orElseThrow(() -> new NotFoundApiException("Room not found"));
    }

    @DELETE
    @Path("{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        if (!store.roomExists(roomId)) {
            throw new NotFoundApiException("Room not found");
        }
        if (store.roomHasActiveSensors(roomId)) {
            throw new ConflictException("Cannot delete room with active sensors");
        }
        store.deleteRoom(roomId);
        return Response.noContent().build();
    }
}
