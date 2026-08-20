package com.globaltrade.logistics.web.resource;

import com.globaltrade.logistics.core.dto.grn.GRNRegistrationRequest;
import com.globaltrade.logistics.core.dto.grn.GRNRegistrationResponse;
import com.globaltrade.logistics.core.service.GRNService;
import jakarta.ejb.EJB;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/grn")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GRNResource {

    @EJB
    private GRNService grnService;

    @POST
    public Response createGRN(@Valid @NotNull GRNRegistrationRequest request) {
        GRNRegistrationResponse response = grnService.createGRN(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/cancel/{grnId}")
    public Response cancelGRN(@PathParam("grnId")UUID grnId) {
        GRNRegistrationResponse response = grnService.cancelGRN(grnId);
        return Response.status(Response.Status.OK).entity(response).build();
    }



}
