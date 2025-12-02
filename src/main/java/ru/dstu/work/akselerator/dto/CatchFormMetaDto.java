package ru.dstu.work.akselerator.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Мета-данные для формы ввода улова:
 * список доступных видов рыбы и регионов
 * для текущей компании (по её мини-квотам).
 */
@Getter
@Setter
public class CatchFormMetaDto {
    private List<CatchFormSpeciesDto> species;
    private List<CatchFormRegionDto> regions;
}
