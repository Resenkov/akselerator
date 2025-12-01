package ru.dstu.work.akselerator.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Краткая сводка по мини-квоте:
 * вид рыбы, регион, уже выловлено и общий лимит.
 */
@Getter
@Setter
public class QuotaUsageSummaryDto {

    private Long quotaId;

    // Вид рыбы
    private Long speciesId;
    private String speciesCommonName;
    private String speciesScientificName;

    // Регион
    private Long regionId;
    private String regionName;
    private String regionCode;

    // Период квоты
    private LocalDate periodStart;
    private LocalDate periodEnd;

    // Лимит и использовано
    private BigDecimal limitKg;
    private BigDecimal usedKg;

    // Дополнительные сведения об организации для полноты ответа
    private Long organizationId;
    private String organizationName;
}
