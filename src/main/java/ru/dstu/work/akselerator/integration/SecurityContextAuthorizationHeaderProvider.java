package ru.dstu.work.akselerator.integration;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityContextAuthorizationHeaderProvider implements AuthorizationHeaderProvider {

    @Override
    public Optional<String> getAuthorizationHeader() {
        var context = SecurityContextHolder.getContext();
        if (context == null || context.getAuthentication() == null) {
            return Optional.empty();
        }
        var authentication = context.getAuthentication();
        if (authentication.getCredentials() instanceof String token) {
            return Optional.of("Bearer " + token);
        }
        if (authentication instanceof AbstractAuthenticationToken authToken
                && authToken.getDetails() instanceof String token) {
            return Optional.of("Bearer " + token);
        }
        return Optional.empty();
    }
}
