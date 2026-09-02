package com.globaltrade.logistics.web.resource;

import com.globaltrade.logistics.core.dto.admin.order.PageResponse;
import com.globaltrade.logistics.core.dto.admin.vendor.AdminVendorDetailsResponse;
import com.globaltrade.logistics.core.dto.admin.vendor.AdminVendorListResponse;
import com.globaltrade.logistics.core.dto.customer.CustomerRegistrationRequest;
import com.globaltrade.logistics.core.dto.customer.CustomerRegistrationResponse;
import com.globaltrade.logistics.core.dto.vendor.VendorRegistrationRequest;
import com.globaltrade.logistics.core.dto.vendor.VendorRegistrationResponse;
import com.globaltrade.logistics.core.entity.vendor.VendorStatus;
import com.globaltrade.logistics.core.service.AdminVendorService;
import com.globaltrade.logistics.core.service.VendorService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/vendors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VendorResource {

    @EJB
    private VendorService vendorService;
    @EJB
    private AdminVendorService adminVendorService;

    @Path("/register")
    @POST
    public Response registerVendor(@Valid @NotNull VendorRegistrationRequest request){
        VendorRegistrationResponse response = vendorService.registerVendor(request);
        return Response.status(Response.Status.CREATED)
                .entity(response)
                .build();
    }

    @GET
    @RolesAllowed({"ADMIN"})
    public Response getVendors(
            @QueryParam("search")
            String search,
            @QueryParam("companyId")
            UUID companyId,
            @QueryParam("status")
            VendorStatus status,
            @QueryParam("sortBy")
            String sortBy,
            @QueryParam("direction")
            String direction,
            @QueryParam("page")
            @DefaultValue("0")
            int page,
            @QueryParam("size")
            @DefaultValue("20")
            int size) {

        PageResponse<AdminVendorListResponse> response =
                adminVendorService.getVendors(search, companyId, status, sortBy, direction, page, size);
        return Response.ok(response).build();
    }

    @GET
    @RolesAllowed({"ADMIN"})
    @Path("/{vendorId}")
    public Response getVendorDetails(@PathParam("vendorId") UUID vendorId) {
        AdminVendorDetailsResponse response = adminVendorService.getVendorDetails(vendorId);
        return Response.ok(response).build();
    }

    @PUT
    @RolesAllowed({"ADMIN"})
    @Path("/activate/{vendorId}")
    public Response activateVendor(@PathParam("vendorId") UUID vendorId) {
        AdminVendorDetailsResponse response = adminVendorService.activateVendor(vendorId);
        return Response.ok(response).build();
    }


    @PUT
    @RolesAllowed({"ADMIN"})
    @Path("/deactivate/{vendorId}")
    public Response deactivateVendor(@PathParam("vendorId") UUID vendorId) {
        AdminVendorDetailsResponse response = adminVendorService.deactivateVendor(vendorId);
        return Response.ok(response).build();
    }

}
