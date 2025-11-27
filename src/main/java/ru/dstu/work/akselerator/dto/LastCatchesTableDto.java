package ru.dstu.work.akselerator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Таблица для отображения топ-3 последних уловов.
 * colums + data, где data — ПОЛНЫЙ CatchReportDto.
 */
@Getter
@Setter
public class LastCatchesTableDto {

    @JsonProperty("colums") // фронтендер так хочет 🙃
    private List<TableColumnDto> columns;

    // ВНИМАНИЕ: тут теперь полный DTO отчёта
    private List<CatchReportDto> data;
}
