package ru.dstu.work.akselerator.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class RegionalQuotaDto {

    private Long id;

    @NotNull private Long speciesId;
    @NotNull private Long regionId;

    @NotNull private LocalDate periodStart;
    @NotNull private LocalDate periodEnd;

    @NotNull @DecimalMin("0.001")
    private BigDecimal limitKg;

    public RegionalQuotaDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSpeciesId() { return speciesId; }
    public void setSpeciesId(Long speciesId) { this.speciesId = speciesId; }

    public Long getRegionId() { return regionId; }
    public void setRegionId(Long regionId) { this.regionId = regionId; }

    public LocalDate getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDate periodStart) { this.periodStart = periodStart; }

    public LocalDate getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDate periodEnd) { this.periodEnd = periodEnd; }

    public BigDecimal getLimitKg() { return limitKg; }
    public void setLimitKg(BigDecimal limitKg) { this.limitKg = limitKg; }
}
