package ru.dstu.work.akselerator.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyRegistrationRequest {
    @NotBlank
    private String orgName;

    @NotBlank
    private String orgType;

    private String inn;

    @NotNull
    private Long regionId;

    @NotBlank
    private String username;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
