package ru.dstu.work.akselerator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Entity
@Table(name = "allocation_quotas", uniqueConstraints = {
        @UniqueConstraint(name = "uq_quota_org_species_region_period", columnNames = {"organization_id","species_id","region_id","period_start","period_end"})
}, indexes = {
        @Index(name = "idx_quotas_lookup", columnList = "organization_id, species_id, region_id, period_start, period_end")
})
@Setter
@NoArgsConstructor
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
}
