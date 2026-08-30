package com.globaltrade.logistics.web.resource;

import com.globaltrade.logistics.core.dto.shipment.ShipmentRegistrationResponse;
import com.globaltrade.logistics.core.dto.shipment.ShipmentTrackingRequest;
import com.globaltrade.logistics.core.dto.shipment.ShipmentTrackingResponse;
import com.globaltrade.logistics.core.service.ShipmentService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/shipments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN","OPERATIONS_MANAGER"})
public class ShipmentResource {

    @EJB
    private ShipmentService shipmentService;

    @PUT
    @Path("/ship/{shipmentId}")
    public Response shipShipment(@PathParam("shipmentId")UUID shipmentId) {
        ShipmentRegistrationResponse response = shipmentService.shipShipment(shipmentId);
        return Response.status(Response.Status.OK).entity(response).build();
    }

    @POST
    @Path("/tracking/{shipmentId}")
    public Response updateTracking(@PathParam("shipmentId")UUID shipmentId, @Valid @NotNull ShipmentTrackingRequest request) {
        ShipmentTrackingResponse response = shipmentService.updateShipmentTracking(shipmentId, request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }
}
