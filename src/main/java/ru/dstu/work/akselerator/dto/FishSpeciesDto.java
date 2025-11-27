package ru.dstu.work.akselerator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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

}
