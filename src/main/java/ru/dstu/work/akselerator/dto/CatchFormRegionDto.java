package ru.dstu.work.akselerator.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Короткое представление региона для формы.
 */
@Getter
@Setter
public class CatchFormRegionDto {
    private Long id;
    private String code;
    private String name;
}
