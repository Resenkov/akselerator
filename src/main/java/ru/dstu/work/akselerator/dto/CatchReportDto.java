package ru.dstu.work.akselerator.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CatchReportDto {
    private Long id;

    @NotNull private Long organizationId;
    @NotNull private Long reportedBy;
    @NotNull private Long speciesId;
    @NotNull private Long regionId;
    @NotNull private LocalDate fishingDate;
    @NotNull private BigDecimal weightKg;
    private String notes;
    private boolean verified = false;

    public CatchReportDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOrganizationId() { return organizationId; }
    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }
    public Long getReportedBy() { return reportedBy; }
    public void setReportedBy(Long reportedBy) { this.reportedBy = reportedBy; }
    public Long getSpeciesId() { return speciesId; }
    public void setSpeciesId(Long speciesId) { this.speciesId = speciesId; }
    public Long getRegionId() { return regionId; }
    public void setRegionId(Long regionId) { this.regionId = regionId; }
    public LocalDate getFishingDate() { return fishingDate; }
    public void setFishingDate(LocalDate fishingDate) { this.fishingDate = fishingDate; }
    public BigDecimal getWeightKg() { return weightKg; }
    public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
}
