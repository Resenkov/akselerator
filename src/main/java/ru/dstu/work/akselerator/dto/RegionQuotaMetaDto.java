package ru.dstu.work.akselerator.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Метаданные для формы создания/редактирования общей региональной квоты.
 * Только рыбы и регионы.
 */
@Getter
@Setter
public class RegionQuotaMetaDto {
    private List<SimpleFishSpeciesDto> species;
    private List<SimpleFishingRegionDto> regions;
}
