package ru.dstu.work.akselerator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "fishing_regions", indexes = {
        @Index(name = "idx_fishing_regions_code", columnList = "code")
})
@Getter
@Setter
public class FishingRegion extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", length = 20, nullable = false, unique = true)
    private String code;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    public FishingRegion() {}

    public FishingRegion(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
