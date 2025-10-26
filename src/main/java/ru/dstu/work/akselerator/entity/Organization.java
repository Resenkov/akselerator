package ru.dstu.work.akselerator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "organizations")
public class Organization extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "org_type", length = 20, nullable = false)
    private String orgType;

    @Column(name = "inn", length = 12)
    private String inn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private FishingRegion region;

    public Organization() {}

    public Organization(String name, String orgType, String inn, FishingRegion region) {
        this.name = name;
        this.orgType = orgType;
        this.inn = inn;
        this.region = region;
    }
}