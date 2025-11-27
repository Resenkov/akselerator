package ru.dstu.work.akselerator.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Доступные виды рыб и регионы для текущей организации (компании).
 */
@Getter
@Setter
public class AvailableSpeciesAndRegionsDto {

    private List<FishSpeciesDto> species;
    private List<FishingRegionDto> regions;
}
