package ru.dstu.work.akselerator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "catch_reports", indexes = {
        @Index(name = "idx_catch_reports_org", columnList = "organization_id"),
        @Index(name = "idx_catch_reports_date", columnList = "fishing_date"),
        @Index(name = "idx_catch_reports_species_region", columnList = "species_id, region_id")
})
@Getter
@Setter
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

}
