package com.globaltrade.logistics.web.resource;

import com.globaltrade.logistics.core.entity.security.Role;
import com.globaltrade.logistics.core.entity.security.RoleType;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/test")
@Produces(MediaType.TEXT_PLAIN)
@Transactional
public class SecurityTestResource {

    @PersistenceContext
    private EntityManager entityManager;

    @GET
    @Path("/public")
    public String publicEndpoint() {

        for (RoleType roleType : RoleType.values()) {
            Role role = new Role();
            role.setName(roleType);
            entityManager.persist(role);
        }

        return "PUBLIC - anyone can access";
    }

    @GET
    @Path("/customer")
    @RolesAllowed("CUSTOMER")
    public String customerEndpoint() {
        return "CUSTOMER - access granted";
    }

    @GET
    @Path("/vendor")
    @RolesAllowed("VENDOR")
    public String vendorEndpoint() {
        return "VENDOR - access granted";
    }

    @GET
    @Path("/admin")
    @RolesAllowed("ADMIN")
    public String adminEndpoint() {
        return "ADMIN - access granted";
    }
}
