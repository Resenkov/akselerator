package ru.dstu.work.akselerator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Строка таблицы с видом рыбы.
 */
@Getter
@Setter
@AllArgsConstructor
public class FishRowDto {
    private Long id;
    private String commonName;
    private String scientificName;
}

