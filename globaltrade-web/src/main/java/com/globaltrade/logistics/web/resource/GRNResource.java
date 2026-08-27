package com.globaltrade.logistics.web.resource;

import com.globaltrade.logistics.core.dto.admin.grn.AdminGRNDetailsResponse;
import com.globaltrade.logistics.core.dto.admin.grn.AdminGRNListResponse;
import com.globaltrade.logistics.core.dto.admin.order.PageResponse;
import com.globaltrade.logistics.core.dto.common.CompanyResponse;
import com.globaltrade.logistics.core.dto.common.VendorOptionResponse;
import com.globaltrade.logistics.core.dto.grn.GRNRegistrationRequest;
import com.globaltrade.logistics.core.dto.grn.GRNRegistrationResponse;
import com.globaltrade.logistics.core.entity.grn.GRNStatus;
import com.globaltrade.logistics.core.service.AdminGRNService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

@Path("/grn")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GRNResource {

    @EJB
    private AdminGRNService adminGrnService;

    @POST
    @Path("/create")
    @RolesAllowed({"ADMIN"})
    public Response createGRN(@Valid @NotNull GRNRegistrationRequest request) {
        GRNRegistrationResponse response = adminGrnService.createGRN(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/cancel/{grnId}")
    public Response cancelGRN(@PathParam("grnId")UUID grnId) {
        GRNRegistrationResponse response = adminGrnService.cancelGRN(grnId);
        return Response.status(Response.Status.OK).entity(response).build();
    }

    @GET
    @Path("/companies")
    public Response getCompanies() {
        List<CompanyResponse> response = adminGrnService.getVendorCompanyOptions();
        return Response.ok(response).build();
    }

    @GET
    @Path("/vendors/{companyId}")
    public Response getVendorsWithCompanies(@PathParam("companyId") UUID companyId) {
        List<VendorOptionResponse> response = adminGrnService.getVendorsByCompanyId(companyId);
        return Response.ok(response).build();
    }

    @GET
    @RolesAllowed({"ADMIN"})
    public Response getGRNs(
            @QueryParam("search") String search,
            @QueryParam("companyId") UUID companyId,
            @QueryParam("warehouseId") UUID warehouseId,
            @QueryParam("status") GRNStatus status,
            @QueryParam("sortBy") String sortBy,
            @QueryParam("direction") String direction,
            @DefaultValue("0")
            @QueryParam("page") int page,
            @DefaultValue("10")
            @QueryParam("size") int size
    ) {

        PageResponse<AdminGRNListResponse> response =
                adminGrnService.getGRNs(
                        search,
                        companyId,
                        warehouseId,
                        status,
                        sortBy,
                        direction,
                        page,
                        size
                );

        return Response.ok(response).build();
    }

    @GET
    @Path("/{grnId}")
    public Response getGRNDetails(@PathParam("grnId") UUID grnId) {
        AdminGRNDetailsResponse response = adminGrnService.getGRNDetails(grnId);
        return Response.ok(response).build();
    }

}
