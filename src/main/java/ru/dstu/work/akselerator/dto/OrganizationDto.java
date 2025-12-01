package ru.dstu.work.akselerator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrganizationDto {
    private Long id;

    @NotBlank @Size(max = 200)
    private String name;

    @NotBlank @Size(max = 20)
    private String orgType;

    @Size(max = 12)
    private String inn;

    private Long regionId;

    public OrganizationDto() {}

    public OrganizationDto(Long id, String name, String orgType, String inn, Long regionId) {
        this.id = id;
        this.name = name;
        this.orgType = orgType;
        this.inn = inn;
        this.regionId = regionId;
    }

}
