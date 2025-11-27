package ru.dstu.work.akselerator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Табличное представление списка уловов:
 * colums + data, где data — уже развёрнутая строка без "сырых" id.
 */
@Getter
@Setter
public class CatchReportsTableDto {

    private List<TableColumnDto> columns;

    private List<CatchReportTableRowDto> data;
}
