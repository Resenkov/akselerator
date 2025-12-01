package ru.dstu.work.akselerator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SimpleFishSpeciesDto {
    private Long id;
    private String scientificName;
    private String commonName;
    private boolean endangered;
}
