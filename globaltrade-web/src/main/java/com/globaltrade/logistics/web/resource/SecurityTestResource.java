package com.globaltrade.logistics.web.resource;

import com.globaltrade.logistics.core.entity.common.Address;
import com.globaltrade.logistics.core.entity.common.Country;
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

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.UUID;

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

//        for (RoleType roleType : RoleType.values()) {
//            Role role = new Role();
//            role.setName(roleType);
//            entityManager.persist(role);
//        }
//
//
//        List<String> countryList = List.of("Sri Lanka", "China","India","Singapore","Malaysia","Japan","Australia","United Arab Emirates","Saudi Arabia","Germany","Netherlands","United Kingdom","France","Switzerland","Canada","United States","Brazil","South Africa","Kenya","South Korea");
//
//        for (String country : countryList) {
//            Country c = new Country(country);
//            entityManager.persist(c);
//        }

//        Country country = entityManager.find(Country.class, UUID.fromString("31daa3f2-9efd-11f1-913b-58733aca4705"));
//
//        Address address = Address.builder()
//                .country(country)
//                .city("Jurong East")
//                .district("Central")
//                .postalCode("12345")
//                .line1("No 66")
//                .line2("New Jurong East")
//                .stateProvince("Johor")
//                .build();
//
//        Warehouse warehouse = Warehouse.builder()
//                .name("GlobalTrade-Warehouse-B")
//                .address(address)
//                .build();
//
//        entityManager.persist(warehouse);

        LocalDateTime dateTime = LocalDateTime.of(2026, Month.AUGUST, 1, 0, 0);

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
