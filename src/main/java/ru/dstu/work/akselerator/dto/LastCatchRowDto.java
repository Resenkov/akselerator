package ru.dstu.work.akselerator.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Строка таблицы "топ-3 последних уловов".
 */
@Getter
@Setter
public class LastCatchRowDto {

    private Long id;

    private LocalDate fishingDate;
    private BigDecimal weightKg;
    private String notes;
    private boolean verified;

    // Для отображения
    private String speciesName;            // вид (рус.)
    private String speciesScientificName;  // вид (лат.)
    private String regionName;             // название региона
    private String regionCode;             // код региона
}
