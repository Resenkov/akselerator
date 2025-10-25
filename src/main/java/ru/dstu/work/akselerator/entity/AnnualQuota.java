package ru.dstu.work.akselerator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Entity
@Table(name = "annual_quotas", uniqueConstraints = {
        @UniqueConstraint(name = "uq_quota_species_region_year", columnNames = {"species_id", "region_id", "year"})
}, indexes = {
        @Index(name = "idx_annual_quotas_year", columnList = "year"),
        @Index(name = "idx_annual_quotas_species_region", columnList = "species_id, region_id")
})
public class AnnualQuota extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id", nullable = false)
    private FishSpecies species;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private FishingRegion region;

    @Setter
    @Column(name = "year", nullable = false)
    private Integer year;

    @Setter
    @Column(name = "limit_kg", precision = 12, scale = 3, nullable = false)
    private BigDecimal limitKg;

    public AnnualQuota() {
    }

    public AnnualQuota(FishSpecies species, FishingRegion region, Integer year, BigDecimal limitKg) {
        this.species = species;
        this.region = region;
        this.year = year;
        this.limitKg = limitKg;
    }

}