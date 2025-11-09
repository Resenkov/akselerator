// ru/dstu/work/akselerator/security/JwtTokenIssuer.java
package ru.dstu.work.akselerator.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenIssuer implements TokenIssuer {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenIssuer(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public String issue(Authentication authentication) {
        // username
        String username = authentication.getName();

        // роли из Authentication -> список строк без префикса ROLE_
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)    // e.g. "ROLE_ADMIN"
                .map(r -> r.replace("ROLE_", ""))       // -> "ADMIN"
                .collect(Collectors.toList());

        return jwtTokenProvider.createToken(username, roles);
    }
}
