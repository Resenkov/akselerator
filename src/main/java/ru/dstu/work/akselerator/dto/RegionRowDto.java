package ru.dstu.work.akselerator.dto;

import lombok.Getter;
import lombok.Setter; /**
 * Строка таблицы по региону.
 */
@Getter
@Setter
public class RegionRowDto {
    private Long id;
    private String code;
    private String name;
}
