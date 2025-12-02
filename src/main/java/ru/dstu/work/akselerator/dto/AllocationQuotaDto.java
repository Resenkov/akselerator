package ru.dstu.work.akselerator.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
public class AllocationQuotaDto {
    private Long id;

    @NotNull
    private Long organizationId;
    private String organizationName;

    @NotNull
    private Long speciesId;
    private String speciesCommonName;
    private String speciesScientificName;

    @NotNull
    private Long regionId;
    private String regionName;
    private String regionCode;

    @NotNull
    private LocalDate periodStart;

    @NotNull
    private LocalDate periodEnd;

    @NotNull
    private BigDecimal limitKg;

    private BigDecimal usedKg;

    public AllocationQuotaDto() {}

}
