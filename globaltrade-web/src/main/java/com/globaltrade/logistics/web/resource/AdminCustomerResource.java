package com.globaltrade.logistics.web.resource;

import com.globaltrade.logistics.core.dto.admin.customer.AdminCustomerDetailsResponse;
import com.globaltrade.logistics.core.dto.admin.customer.AdminCustomerListResponse;
import com.globaltrade.logistics.core.dto.admin.order.PageResponse;
import com.globaltrade.logistics.core.dto.common.CompanyResponse;
import com.globaltrade.logistics.core.entity.customer.CustomerStatus;
import com.globaltrade.logistics.core.entity.customer.CustomerType;
import com.globaltrade.logistics.core.service.AdminCustomerService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

@Path("/admin/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
public class AdminCustomerResource {
    @EJB
    private AdminCustomerService adminCustomerService;


    @GET
    public Response getCustomers(
            @QueryParam("search")
            String search,
            @QueryParam("companyId")
            UUID companyId,
            @QueryParam("customerType")
            CustomerType customerType,
            @QueryParam("status")
            CustomerStatus status,
            @QueryParam("kycVerified")
            Boolean kycVerified,
            @QueryParam("sortBy")
            @DefaultValue("customerNumber")
            String sortBy,
            @QueryParam("direction")
            @DefaultValue("ASC")
            String direction,
            @QueryParam("page")
            @DefaultValue("0")
            int page,
            @QueryParam("size")
            @DefaultValue("10")
            int size
    ) {

        PageResponse<AdminCustomerListResponse> response =
                adminCustomerService.getCustomers(
                        search,
                        companyId,
                        customerType,
                        status,
                        kycVerified,
                        sortBy,
                        direction,
                        page,
                        size
                );

        return Response.ok(response).build();
    }

    @GET
    @Path("/{customerId}")
    public Response getCustomerDetails(@PathParam("customerId") UUID customerId) {
        AdminCustomerDetailsResponse response = adminCustomerService.getCustomerDetails(customerId);
        return Response.ok(response).build();
    }

    @GET
    @Path("/companies")
    public Response getCustomerCompanies() {
        List<CompanyResponse> response = adminCustomerService.getCustomerCompanies();
        return Response.ok(response).build();
    }


}
