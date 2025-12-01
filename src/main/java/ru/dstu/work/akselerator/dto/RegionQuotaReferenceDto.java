package ru.dstu.work.akselerator.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RegionQuotaReferenceDto {
    private List<FishSpeciesDto> fishSpecies;
    private List<FishingRegionDto> regions;

    public RegionQuotaReferenceDto() {
    }

    public RegionQuotaReferenceDto(List<FishSpeciesDto> fishSpecies, List<FishingRegionDto> regions) {
        this.fishSpecies = fishSpecies;
        this.regions = regions;
    }
}
