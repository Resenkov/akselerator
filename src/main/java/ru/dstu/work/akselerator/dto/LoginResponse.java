package ru.dstu.work.akselerator.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LoginResponse {

    private String token;
    private String tokenType = "Bearer";

    private UserDto user;
    private OrganizationDto organization;

    private List<String> roles;

    /**
     * Indicates whether the supplied token is expired. Useful for token introspection
     * responses. For successful logins/registrations this is always {@code false}.
     */
    private boolean expired = false;

    /**
     * Indicates whether the supplied token is currently valid (signature is correct and
     * not expired). For successful logins/registrations this is always {@code true}.
     */
    private boolean valid = true;

    public LoginResponse() {
    }

    public LoginResponse(String token, String tokenType, UserDto user, OrganizationDto organization, List<String> roles) {
        this(token, tokenType, user, organization, roles, false, true);
    }

    public LoginResponse(String token,
                         String tokenType,
                         UserDto user,
                         OrganizationDto organization,
                         List<String> roles,
                         boolean expired,
                         boolean valid) {
        this.token = token;
        this.tokenType = tokenType;
        this.user = user;
        this.organization = organization;
        this.roles = roles;
        this.expired = expired;
        this.valid = valid;
    }
}
