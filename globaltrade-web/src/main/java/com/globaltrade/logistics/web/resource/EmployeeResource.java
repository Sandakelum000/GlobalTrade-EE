package com.globaltrade.logistics.web.resource;

import com.globaltrade.logistics.core.dto.employee.EmployeeRegistrationRequest;
import com.globaltrade.logistics.core.dto.employee.EmployeeRegistrationResponse;
import com.globaltrade.logistics.core.entity.employee.EmployeeDepartment;
import com.globaltrade.logistics.core.entity.employee.EmployeeStatus;
import com.globaltrade.logistics.core.service.AdminEmployeeService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

@Path("/employee")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN"})
public class EmployeeResource {

    @EJB
    private AdminEmployeeService adminEmployeeService;

    @Path("/register")
    @POST
    public Response registerEmployee(@NotNull @Valid EmployeeRegistrationRequest request) {
        EmployeeRegistrationResponse response = adminEmployeeService.registerEmployee(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @RolesAllowed({"ADMIN"})
    public Response getEmployees(
            @QueryParam("search") String search,
            @QueryParam("department") EmployeeDepartment department,
            @QueryParam("status") EmployeeStatus status,
            @QueryParam("sortBy") String sortBy,
            @QueryParam("direction") String direction,
            @QueryParam("page")
            @DefaultValue("0") int page,
            @QueryParam("size")
            @DefaultValue("20") int size) {

        return Response.ok(
                adminEmployeeService.getEmployees(
                        search,
                        department,
                        status,
                        sortBy,
                        direction,
                        page,
                        size
                )
        ).build();
    }

    @GET
    @Path("/{employeeId}")
    public Response getEmployeeDetails(
            @PathParam("employeeId") UUID employeeId) {
        return Response.ok(adminEmployeeService.getEmployeeDetails(employeeId)).build();
    }
}
