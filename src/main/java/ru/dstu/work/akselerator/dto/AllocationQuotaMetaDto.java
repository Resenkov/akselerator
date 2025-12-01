package ru.dstu.work.akselerator.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Метаданные для формы создания/редактирования мини-квот.
 * Рыбы, регионы и организации.
 */
@Getter
@Setter
public class AllocationQuotaMetaDto {
    private List<SimpleFishSpeciesDto> species;
    private List<SimpleFishingRegionDto> regions;
    private List<SimpleOrganizationDto> organizations;
}
