package com.globaltrade.logistics.web.resource;

import com.globaltrade.logistics.core.dto.admin.audit.AdminAuditDetailsResponse;
import com.globaltrade.logistics.core.dto.admin.audit.AdminAuditListResponse;
import com.globaltrade.logistics.core.dto.admin.order.PageResponse;
import com.globaltrade.logistics.core.entity.audit.AuditAction;
import com.globaltrade.logistics.core.service.AdminAuditService;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.UUID;

@Path("/admin/audits")
@Produces(MediaType.APPLICATION_JSON)
public class AdminAuditResource {
    @EJB
    private AdminAuditService adminAuditService;


    @GET
    public Response getAudits(
            @QueryParam("search")
            String search,
            @QueryParam("action")
            AuditAction action,
            @QueryParam("userId")
            UUID userId,
            @QueryParam("from")
            String from,
            @QueryParam("to")
            String to,
            @QueryParam("sortBy")
            @DefaultValue("timestamp")
            String sortBy,
            @QueryParam("direction")
            @DefaultValue("DESC")
            String direction,
            @QueryParam("page")
            @DefaultValue("0")
            int page,
            @QueryParam("size")
            @DefaultValue("20")
            int size) {

        LocalDateTime fromDate = from != null && !from.isBlank() ? LocalDateTime.parse(from) : null;
        LocalDateTime toDate = to != null && !to.isBlank() ? LocalDateTime.parse(to) : null;

        PageResponse<AdminAuditListResponse> response = adminAuditService.getAudits(search, action, userId,
                fromDate, toDate, sortBy, direction, page, size);


        return Response.ok(response).build();
    }


    @GET
    @Path("/{auditId}")
    public Response getAuditDetails(@PathParam("auditId") UUID auditId) {
        AdminAuditDetailsResponse response = adminAuditService.getAuditDetails(auditId);
        return Response.ok(response).build();
    }
}
