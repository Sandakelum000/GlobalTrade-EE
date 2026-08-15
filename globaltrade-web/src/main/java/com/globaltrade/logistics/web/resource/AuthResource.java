package com.globaltrade.logistics.web.resource;

import com.globaltrade.logistics.core.entity.security.Role;
import com.globaltrade.logistics.core.entity.security.RoleType;
import com.globaltrade.logistics.core.entity.security.User;
import com.globaltrade.logistics.ejb.security.JWTService;
import com.globaltrade.logistics.ejb.security.PasswordService;
import com.globaltrade.logistics.web.dto.LoginRequest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    private IdentityStoreHandler identityStoreHandler;

    @Inject
    private JWTService jwtService;

    @POST
    @Path("/login")
    public Response login(@Valid LoginRequest request){
        CredentialValidationResult result = identityStoreHandler
                .validate(new UsernamePasswordCredential(request.username(), request.password()));

        if(result.getStatus() == CredentialValidationResult.Status.VALID) {
            String token = jwtService.generateAccessToken(result.getCallerPrincipal().getName(), result.getCallerGroups());

            //refresh token
            return Response.ok(
                    Map.of("access",token,
                            "roles",result.getCallerGroups())).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }


}
