package ru.dstu.work.akselerator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "fish_species", indexes = {
        @Index(name = "idx_fish_species_scientific", columnList = "scientific_name")
})
@Getter
@Setter
public class FishSpecies extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scientific_name", length = 100, nullable = false, unique = true)
    private String scientificName;

    @Column(name = "common_name", length = 100, nullable = false)
    private String commonName;

    @Column(name = "is_endangered", nullable = false)
    private boolean endangered = false;

    public FishSpecies() {}

    public FishSpecies(String scientificName, String commonName) {
        this.scientificName = scientificName;
        this.commonName = commonName;
    }
}
