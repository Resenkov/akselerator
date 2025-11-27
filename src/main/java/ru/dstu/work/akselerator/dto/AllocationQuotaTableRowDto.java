package ru.dstu.work.akselerator.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Строка таблицы для мини-квоты (AllocationQuota),
 * с развёрнутыми значениями по связям.
 */
@Getter
@Setter
public class AllocationQuotaTableRowDto {

    private Long id;

    // Организация
    private Long organizationId;
    private String organizationName;

    // Вид рыбы
    private Long speciesId;
    private String speciesCommonName;
    private String speciesScientificName;

    // Регион
    private Long regionId;
    private String regionName;
    private String regionCode;

    // Период и лимит
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private BigDecimal limitKg;

    // НОВОЕ: суммарный вылов по этой квоте
    private BigDecimal usedKg;
}
