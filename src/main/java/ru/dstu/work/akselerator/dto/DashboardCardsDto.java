package ru.dstu.work.akselerator.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class DashboardCardsDto {
    private LocalDate date;
    private BigDecimal totalCatchKg;
    private Long companiesCount;
    private Long regionsCount;
    private BigDecimal averageCatchKg;
}
