package com.globaltrade.logistics.web.config;

import jakarta.annotation.security.DeclareRoles;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
@DeclareRoles({
        "ADMIN",
        "OPERATIONS_MANAGER",
        "LOGISTICS_OFFICER",
        "WAREHOUSE_MANAGER",
        "CUSTOMER",
        "VENDOR"
})
public class ApplicationConfig extends Application {

}
