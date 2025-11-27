package ru.dstu.work.akselerator.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Универсальный DTO для таблицы: колонки + строки.
 */
@Getter
@Setter
public class TableDto<T> {
    private List<TableColumnDto> columns;
    private List<T> data;
}
