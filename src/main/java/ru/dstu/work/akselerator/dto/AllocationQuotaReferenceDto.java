package ru.dstu.work.akselerator.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AllocationQuotaReferenceDto extends RegionQuotaReferenceDto {
    private List<OrganizationDto> organizations;

    public AllocationQuotaReferenceDto() {
        super();
    }

    public AllocationQuotaReferenceDto(List<FishSpeciesDto> fishSpecies,
                                       List<FishingRegionDto> regions,
                                       List<OrganizationDto> organizations) {
        super(fishSpecies, regions);
        this.organizations = organizations;
    }
}
