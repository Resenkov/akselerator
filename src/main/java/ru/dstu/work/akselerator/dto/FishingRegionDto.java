package ru.dstu.work.akselerator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FishingRegionDto {
    private Long id;

    @NotBlank
    @Size(max = 20)
    private String code;

    @NotBlank
    @Size(max = 100)
    private String name;

    public FishingRegionDto() {}

    public FishingRegionDto(Long id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
