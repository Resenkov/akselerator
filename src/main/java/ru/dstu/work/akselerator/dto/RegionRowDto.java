package ru.dstu.work.akselerator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter; /**
 * Строка таблицы с регионом.
 */
@Getter
@Setter
@AllArgsConstructor
public class RegionRowDto {
    private Long id;
    private String code;
    private String name;
}
