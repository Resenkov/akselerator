package ru.dstu.work.akselerator.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
public class RegionTotalQuotaDto {
    private Long id;

    @NotNull private Long regionId;
    private String regionName;
    private String regionCode;
    @NotNull private Long speciesId;
    private String speciesCommonName;
    private String speciesScientificName;
    @NotNull private LocalDate periodStart;
    @NotNull private LocalDate periodEnd;

    @NotNull @DecimalMin("0.001")
    private BigDecimal limitKg;

}
