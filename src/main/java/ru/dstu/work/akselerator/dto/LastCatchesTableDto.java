package ru.dstu.work.akselerator.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LastCatchesTableDto {

    private List<TableColumnDto> columns;

    // здесь уже человекочитаемые строки таблицы
    private List<CatchReportTableRowDto> data;
}
