package ru.dstu.work.akselerator.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";
    public LoginResponse() {}
    public LoginResponse(String token, String tokenType) { this.token = token; this.tokenType = tokenType; }
}
