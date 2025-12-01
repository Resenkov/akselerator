package ru.dstu.work.akselerator.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

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

    public AllocationQuotaDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOrganizationId() { return organizationId; }
    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }
    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
    public Long getSpeciesId() { return speciesId; }
    public void setSpeciesId(Long speciesId) { this.speciesId = speciesId; }
    public String getSpeciesCommonName() { return speciesCommonName; }
    public void setSpeciesCommonName(String speciesCommonName) { this.speciesCommonName = speciesCommonName; }
    public String getSpeciesScientificName() { return speciesScientificName; }
    public void setSpeciesScientificName(String speciesScientificName) { this.speciesScientificName = speciesScientificName; }
    public Long getRegionId() { return regionId; }
    public void setRegionId(Long regionId) { this.regionId = regionId; }
    public String getRegionName() { return regionName; }
    public void setRegionName(String regionName) { this.regionName = regionName; }
    public String getRegionCode() { return regionCode; }
    public void setRegionCode(String regionCode) { this.regionCode = regionCode; }
    public LocalDate getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDate periodStart) { this.periodStart = periodStart; }
    public LocalDate getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDate periodEnd) { this.periodEnd = periodEnd; }
    public BigDecimal getLimitKg() { return limitKg; }
    public void setLimitKg(BigDecimal limitKg) { this.limitKg = limitKg; }
}
