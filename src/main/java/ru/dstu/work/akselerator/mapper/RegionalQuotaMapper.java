package ru.dstu.work.akselerator.mapper;

import ru.dstu.work.akselerator.dto.RegionalQuotaDto;
import ru.dstu.work.akselerator.entity.RegionalQuota;
import ru.dstu.work.akselerator.entity.FishSpecies;
import ru.dstu.work.akselerator.entity.FishingRegion;

public class RegionalQuotaMapper {

    public static RegionalQuotaDto toDto(RegionalQuota e) {
        if (e == null) return null;
        RegionalQuotaDto d = new RegionalQuotaDto();
        d.setId(e.getId());
        d.setSpeciesId(e.getSpecies() == null ? null : e.getSpecies().getId());
        d.setRegionId(e.getRegion() == null ? null : e.getRegion().getId());
        d.setPeriodStart(e.getPeriodStart());
        d.setPeriodEnd(e.getPeriodEnd());
        d.setLimitKg(e.getLimitKg());
        return d;
    }

    public static RegionalQuota toEntity(RegionalQuotaDto d) {
        if (d == null) return null;
        RegionalQuota e = new RegionalQuota();
        if (d.getId() != null) e.setId(d.getId());
        if (d.getSpeciesId() != null) {
            FishSpecies s = new FishSpecies();
            s.setId(d.getSpeciesId());
            e.setSpecies(s);
        }
        if (d.getRegionId() != null) {
            FishingRegion r = new FishingRegion();
            r.setId(d.getRegionId());
            e.setRegion(r);
        }
        e.setPeriodStart(d.getPeriodStart());
        e.setPeriodEnd(d.getPeriodEnd());
        e.setLimitKg(d.getLimitKg());
        return e;
    }
}
