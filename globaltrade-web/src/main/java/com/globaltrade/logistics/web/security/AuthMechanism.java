package com.globaltrade.logistics.web.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.globaltrade.logistics.ejb.security.JWTService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@ApplicationScoped
public class AuthMechanism implements HttpAuthenticationMechanism {

    @Inject
    private JWTService jwtService;

    private static final Logger LOGGER = Logger.getLogger(AuthMechanism.class.getName());

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext context) throws AuthenticationException {
        String authHeader = request.getHeader("Authorization");

        if(authHeader == null || authHeader.isBlank()){
            return handleUnAuthenticated(context);
        }

        if(!authHeader.regionMatches(true,0,"Bearer ",0,7)){
            return context.responseUnauthorized();
        }
        String token = authHeader.substring(7).trim();
        if(token.isEmpty()){
            return context.responseUnauthorized();
        }
        //jwt validation
        try{
            if (!jwtService.isValidToken(token)) {
                return context.responseUnauthorized();
            }
            DecodedJWT decodedToken = jwtService.decode(token);
            String username = decodedToken.getSubject();

            if (username == null || username.isBlank()) {
                return context.responseUnauthorized();
            }

            List<String> roleList = decodedToken.getClaim("roles").asList(String.class);

            Set<String> roles = roleList == null ? Set.of() : new HashSet<>(roleList);
            return context.notifyContainerAboutLogin(username, roles);

        }catch (Exception e){
            return context.responseUnauthorized();
        }
    }

    private AuthenticationStatus handleUnAuthenticated(HttpMessageContext context){
        if(context.isProtected()){
            return context.responseUnauthorized();
        }
        return context.doNothing();
    }
}
