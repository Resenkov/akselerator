package ru.dstu.work.akselerator.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class AllocationQuotaDto {
    private Long id;

    @NotNull
    private Long organizationId;

    @NotNull
    private Long speciesId;

    @NotNull
    private Long regionId;

    @NotNull
    private LocalDate periodStart;

    @NotNull
    private LocalDate periodEnd;

    @NotNull
    private BigDecimal limitKg;

    public AllocationQuotaDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOrganizationId() { return organizationId; }
    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }
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
