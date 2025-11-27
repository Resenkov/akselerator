package ru.dstu.work.akselerator.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyRegistrationResponse {

    private Long organizationId;
    private String organizationName;

    private Long userId;
    private String username;
    private String email;
}
