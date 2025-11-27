package ru.dstu.work.akselerator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Описание колонки таблицы для фронта.
 */
@Getter
@Setter
@AllArgsConstructor
public class TableColumnDto {
    private String header;
    private String accessorKey;
}
