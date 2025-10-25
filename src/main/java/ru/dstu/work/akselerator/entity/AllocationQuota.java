package ru.dstu.work.akselerator.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "allocation_quotas", uniqueConstraints = {
        @UniqueConstraint(name = "uq_quota_org_species_region_period", columnNames = {"organization_id","species_id","region_id","period_start","period_end"})
}, indexes = {
        @Index(name = "idx_quotas_lookup", columnList = "organization_id, species_id, region_id, period_start, period_end")
})
public class AllocationQuota extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id", nullable = false)
    private FishSpecies species;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private FishingRegion region;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(name = "limit_kg", precision = 12, scale = 3, nullable = false)
    private BigDecimal limitKg;

    public AllocationQuota() {}

    public Long getId() { return id; }
    public Organization getOrganization() { return organization; }
    public void setOrganization(Organization organization) { this.organization = organization; }
    public FishSpecies getSpecies() { return species; }
    public void setSpecies(FishSpecies species) { this.species = species; }
    public FishingRegion getRegion() { return region; }
    public void setRegion(FishingRegion region) { this.region = region; }
    public LocalDate getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDate periodStart) { this.periodStart = periodStart; }
    public LocalDate getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDate periodEnd) { this.periodEnd = periodEnd; }
    public BigDecimal getLimitKg() { return limitKg; }
    public void setLimitKg(BigDecimal limitKg) { this.limitKg = limitKg; }
}
