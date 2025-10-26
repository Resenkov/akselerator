package ru.dstu.work.akselerator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FishSpeciesDto {
    private Long id;

    @NotBlank @Size(max = 100)
    private String scientificName;

    @NotBlank @Size(max = 100)
    private String commonName;

    private boolean endangered;

    public FishSpeciesDto() {}

    public FishSpeciesDto(Long id, String scientificName, String commonName, boolean endangered) {
        this.id = id;
        this.scientificName = scientificName;
        this.commonName = commonName;
        this.endangered = endangered;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getScientificName() { return scientificName; }
    public void setScientificName(String scientificName) { this.scientificName = scientificName; }
    public String getCommonName() { return commonName; }
    public void setCommonName(String commonName) { this.commonName = commonName; }
    public boolean isEndangered() { return endangered; }
    public void setEndangered(boolean endangered) { this.endangered = endangered; }
}
