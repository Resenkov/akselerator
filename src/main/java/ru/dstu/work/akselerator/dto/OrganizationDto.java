package ru.dstu.work.akselerator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class OrganizationDto {
    private Long id;

    @NotBlank @Size(max = 200)
    private String name;

    @NotBlank @Size(max = 20)
    private String orgType;

    @Size(max = 12)
    private String inn;

    @NotNull
    private Long regionId;

    public OrganizationDto() {}

    public OrganizationDto(Long id, String name, String orgType, String inn, Long regionId) {
        this.id = id;
        this.name = name;
        this.orgType = orgType;
        this.inn = inn;
        this.regionId = regionId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getOrgType() { return orgType; }
    public void setOrgType(String orgType) { this.orgType = orgType; }
    public String getInn() { return inn; }
    public void setInn(String inn) { this.inn = inn; }
    public Long getRegionId() { return regionId; }
    public void setRegionId(Long regionId) { this.regionId = regionId; }
}
