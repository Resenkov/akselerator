package ru.dstu.work.akselerator.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {

    private String token;
    private String tokenType = "Bearer";

    private UserDto user;
    private OrganizationDto organization;

    public LoginResponse() {
    }

    public LoginResponse(String token, String tokenType, UserDto user, OrganizationDto organization) {
        this.token = token;
        this.tokenType = tokenType;
        this.user = user;
        this.organization = organization;
    }
}
