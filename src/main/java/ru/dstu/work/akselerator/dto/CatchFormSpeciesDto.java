package ru.dstu.work.akselerator.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Короткое представление вида рыбы для формы.
 */
@Getter
@Setter
public class CatchFormSpeciesDto {
    private Long id;
    private String scientificName;
    private String commonName;
    private boolean endangered;
}
