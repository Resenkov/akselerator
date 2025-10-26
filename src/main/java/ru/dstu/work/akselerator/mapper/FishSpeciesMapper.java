package ru.dstu.work.akselerator.mapper;

import ru.dstu.work.akselerator.dto.FishSpeciesDto;
import ru.dstu.work.akselerator.entity.FishSpecies;

public class FishSpeciesMapper {
    public static FishSpeciesDto toDto(FishSpecies e) {
        if (e == null) return null;
        return new FishSpeciesDto(e.getId(), e.getScientificName(), e.getCommonName(), e.isEndangered());
    }
    public static FishSpecies toEntity(FishSpeciesDto d) {
        if (d == null) return null;
        FishSpecies e = new FishSpecies();
        e.setScientificName(d.getScientificName());
        e.setCommonName(d.getCommonName());
        e.setEndangered(d.isEndangered());
        return e;
    }
}
