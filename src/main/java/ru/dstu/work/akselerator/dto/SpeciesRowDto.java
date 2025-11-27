package ru.dstu.work.akselerator.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Строка таблицы по виду рыбы.
 * Поля именуем так, как будет удобно фронту в accessorKey.
 */
@Getter
@Setter
public class SpeciesRowDto {
    private Long id;
    private String scientificName;
    private String commonName;
    private boolean endangered;
}

