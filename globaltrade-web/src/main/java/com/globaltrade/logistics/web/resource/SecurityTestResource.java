package com.globaltrade.logistics.web.resource;

import com.globaltrade.logistics.core.entity.common.Address;
import com.globaltrade.logistics.core.entity.security.Role;
import com.globaltrade.logistics.core.entity.security.RoleType;
import com.globaltrade.logistics.core.entity.warehouse.Warehouse;
import com.globaltrade.logistics.ejb.beans.TestBean;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.EJB;
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

    @EJB
    private TestBean testBean;

    @GET
    @Path("/public")
    public String publicEndpoint() {

        for (RoleType roleType : RoleType.values()) {
            Role role = new Role();
            role.setName(roleType);
            entityManager.persist(role);
        }

        Address address = Address.builder()
                .country("Sri Lanka")
                .city("Colombo")
                .district("Colombo")
                .postalCode("12345")
                .line1("No 45")
                .line2("New Lotus Road")
                .stateProvince("Western")
                .build();

        Warehouse warehouse = Warehouse.builder()
                .name("GlobalTrade-Warehouse-A")
                .address(address)
                .build();

        entityManager.persist(warehouse);

        return "PUBLIC - anyone can access";
    }

    @GET
    @Path("/it")
    //@RolesAllowed("CUSTOMER")
    public String interceptEndpoint() {
        testBean.m();
        return "Resource called";
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
