package ru.dstu.work.akselerator.mapper;

import ru.dstu.work.akselerator.dto.FishingRegionDto;
import ru.dstu.work.akselerator.entity.FishingRegion;

public class FishingRegionMapper {
    public static FishingRegionDto toDto(FishingRegion e) {
        if (e == null) return null;
        return new FishingRegionDto(e.getId(), e.getCode(), e.getName());
    }
    public static FishingRegion toEntity(FishingRegionDto d) {
        if (d == null) return null;
        FishingRegion e = new FishingRegion();
        e.setCode(d.getCode());
        e.setName(d.getName());
        return e;
    }
}
