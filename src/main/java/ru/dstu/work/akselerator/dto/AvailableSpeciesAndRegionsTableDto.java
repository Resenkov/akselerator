package ru.dstu.work.akselerator.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Табличное представление доступных видов и регионов
 * для текущей организации.
 */
@Getter
@Setter
public class AvailableSpeciesAndRegionsTableDto {

    private TableDto<SpeciesRowDto> species;
    private TableDto<RegionRowDto> regions;
}
