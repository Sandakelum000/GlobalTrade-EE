package com.globaltrade.logistics.web.resource;

import com.globaltrade.logistics.core.dto.login.LoginRequest;
import com.globaltrade.logistics.core.dto.login.RefreshRequest;
import com.globaltrade.logistics.core.entity.security.User;
import com.globaltrade.logistics.core.service.AuditLogService;
import com.globaltrade.logistics.ejb.security.JWTService;
import com.globaltrade.logistics.ejb.security.RefreshTokenService;

import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    private IdentityStoreHandler identityStoreHandler;
    @Inject
    private JWTService jwtService;
    @Inject
    private RefreshTokenService refreshTokenService;
    @EJB
    private AuditLogService  auditLogService;
    @Context
    private SecurityContext securityContext;

    @POST
    @Path("/login")
    public Response login(@Valid @NotNull LoginRequest request) {
        CredentialValidationResult result = identityStoreHandler
                .validate(new UsernamePasswordCredential(request.username(), request.password()));

        if (result.getStatus() != CredentialValidationResult.Status.VALID) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "Invalid username or password"))
                    .build();
        }
        String username = result.getCallerPrincipal().getName();
        Set<String> roles = result.getCallerGroups();

        try {
            String accessToken = jwtService.generateAccessToken(username, roles);
            String refreshToken = refreshTokenService.createByUsername(username);

            //auditLogService.logLogin(username);

            return Response.ok(
                    Map.of("access", accessToken,
                            "refreshToken", refreshToken,
                            "username", username,
                            "roles", result.getCallerGroups())).build();
        } catch (Exception e) {
            LoggerFactory.getLogger(AuthResource.class).error("Login token creation failed for user: {}", username, e);
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "Request token creation failed"))
                    .build();
        }
    }

    @Path("/refresh")
    @POST
    public Response refresh(@Valid @NotNull RefreshRequest request) {
        if (request == null || request.refreshToken() == null || request.refreshToken().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", "Refresh token is required"))
                    .build();
        }
        String oldRefreshToken = request.refreshToken();

        String newRefreshToken = "";
        try {
            RefreshTokenService.RefreshResult result = refreshTokenService.rotateRfToken(oldRefreshToken);
            newRefreshToken = result.refreshToken();
            User user = result.user();

            Set<String> userRoles = user.getRoles()
                    .stream()
                    .map(role -> role.getName().name())
                    .collect(Collectors.toSet());
            String newAccessToken = jwtService.generateAccessToken(user.getUsername(), userRoles);
            return Response.ok(
                    Map.of(
                            "access", newAccessToken,
                            "refresh", newRefreshToken,
                            "username", user.getUsername(),
                            "roles", userRoles
                    )).build();
        } catch (IllegalArgumentException e) {
            return Response.status(
                            Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", "Invalid or expired refresh token"))
                    .build();
        }
    }

    @POST
    @Path("/logout")
    public Response logout() {
        String username = securityContext.getUserPrincipal() != null
                ? securityContext.getUserPrincipal().getName()
                : null;
        if (username != null) {auditLogService.logLogout(username);}

        return Response.ok(
                Map.of("message", "Logged out successfully")
        ).build();
    }

}
