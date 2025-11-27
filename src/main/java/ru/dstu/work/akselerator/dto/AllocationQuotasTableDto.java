package ru.dstu.work.akselerator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Табличное представление списка мини-квот.
 * colums + data, где data — AllocationQuotaTableRowDto.
 */
@Getter
@Setter
public class AllocationQuotasTableDto {


    private List<TableColumnDto> columns;

    private List<AllocationQuotaTableRowDto> data;
}
