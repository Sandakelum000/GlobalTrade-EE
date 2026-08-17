package com.globaltrade.logistics.web.resource;

import com.globaltrade.logistics.core.dto.employee.EmployeeRegistrationRequest;
import com.globaltrade.logistics.core.dto.employee.EmployeeRegistrationResponse;
import com.globaltrade.logistics.core.service.EmployeeService;
import jakarta.ejb.EJB;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/employee")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmployeeResource {

    @EJB
    private EmployeeService employeeService;

    @Path("/register")
    @POST
    public Response registerEmployee(@NotNull @Valid EmployeeRegistrationRequest request) {
        EmployeeRegistrationResponse response = employeeService.registerEmployee(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

}
