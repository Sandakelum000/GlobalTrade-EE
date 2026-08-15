package com.globaltrade.logistics.ejb.security;

import com.globaltrade.logistics.core.entity.security.Role;
import com.globaltrade.logistics.core.entity.security.RoleType;
import com.globaltrade.logistics.core.entity.security.User;
import com.globaltrade.logistics.ejb.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class AppIdentityStore implements IdentityStore {
    @Inject
    private UserRepository userRepository;

    @Inject
    private PasswordService passwordService;

    @Override
    public CredentialValidationResult validate(Credential credential) {
        if(!(credential instanceof UsernamePasswordCredential upc)) {
             return CredentialValidationResult.INVALID_RESULT;
        }

        String username = upc.getCaller();
        String password = upc.getPasswordAsString();

        Optional<User> userOptional = userRepository.findByUsername(username);
        if(userOptional.isEmpty()) {
            return CredentialValidationResult.INVALID_RESULT;
        }

        User user = userOptional.get();
        if(!passwordService.verifyPassword(password,user.getPasswordHash())) {
            return CredentialValidationResult.INVALID_RESULT;
        }

        Set<String> roles = user.getRoles()
                .stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        return new CredentialValidationResult(username, roles);
    }

}
