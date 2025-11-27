package ru.dstu.work.akselerator.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Строка таблицы для отображения уловов компании.
 * Здесь уже не id, а человекочитаемые значения по связям.
 */
@Getter
@Setter
public class CatchReportTableRowDto {

    // можно оставить id отчёта, это полезно для действий (редактирование/детали)
    private Long id;

    // вместо organizationId
    private String organizationName;

    // вместо reportedById
    private String reportedByUsername;

    // вместо speciesId
    private String speciesCommonName;
    private String speciesScientificName;

    // вместо regionId
    private String regionName;
    private String regionCode;

    // остальные поля отчёта
    private LocalDate fishingDate;
    private BigDecimal weightKg;
    private String notes;
    private boolean verified;
}
