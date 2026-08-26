package com.globaltrade.logistics.web.resource;

import com.globaltrade.logistics.core.dto.common.CompanyResponse;
import com.globaltrade.logistics.core.dto.common.CountryResponse;
import com.globaltrade.logistics.core.service.ContentService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

@Path("/data")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ContentResource {

    @EJB
    private ContentService contentService;

    @GET
    @Path("/countries")
    public Response getCountries() {
        List<CountryResponse> responses = contentService.getCountries();
        return Response.ok(responses).build();
    }

    @GET
    @Path("/{countryId}/companies")
    public Response getCompanyByCountry(@PathParam("countryId")UUID countryId) {
        List<CompanyResponse> responses = contentService.getCompaniesByCountryId(countryId);
        return Response.ok(responses).build();
    }
}
