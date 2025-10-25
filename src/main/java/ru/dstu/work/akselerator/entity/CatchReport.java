package ru.dstu.work.akselerator.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "catch_reports", indexes = {
        @Index(name = "idx_catch_reports_org", columnList = "organization_id"),
        @Index(name = "idx_catch_reports_date", columnList = "fishing_date"),
        @Index(name = "idx_catch_reports_species_region", columnList = "species_id, region_id")
})
public class CatchReport extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by", nullable = false)
    private User reportedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id", nullable = false)
    private FishSpecies species;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private FishingRegion region;

    @Column(name = "fishing_date", nullable = false)
    private LocalDate fishingDate;

    @Column(name = "weight_kg", precision = 12, scale = 3, nullable = false)
    private BigDecimal weightKg;

    @Column(name = "notes")
    private String notes;

    @Column(name = "is_verified", nullable = false)
    private boolean verified = false;

    public CatchReport() {}

    public Long getId() { return id; }
    public Organization getOrganization() { return organization; }
    public void setOrganization(Organization organization) { this.organization = organization; }
    public User getReportedBy() { return reportedBy; }
    public void setReportedBy(User reportedBy) { this.reportedBy = reportedBy; }
    public FishSpecies getSpecies() { return species; }
    public void setSpecies(FishSpecies species) { this.species = species; }
    public FishingRegion getRegion() { return region; }
    public void setRegion(FishingRegion region) { this.region = region; }
    public LocalDate getFishingDate() { return fishingDate; }
    public void setFishingDate(LocalDate fishingDate) { this.fishingDate = fishingDate; }
    public BigDecimal getWeightKg() { return weightKg; }
    public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
}
