package ru.dstu.work.akselerator.mapper;

import ru.dstu.work.akselerator.dto.RegionTotalQuotaDto;
import ru.dstu.work.akselerator.entity.RegionTotalQuota;
import ru.dstu.work.akselerator.entity.FishingRegion;

public class RegionTotalQuotaMapper {

    public static RegionTotalQuotaDto toDto(RegionTotalQuota e) {
        if (e == null) return null;
        RegionTotalQuotaDto d = new RegionTotalQuotaDto();
        d.setId(e.getId());
        if (e.getRegion() != null) {
            d.setRegionId(e.getRegion().getId());
            d.setRegionName(e.getRegion().getName());
            d.setRegionCode(e.getRegion().getCode());
        }
        d.setPeriodStart(e.getPeriodStart());
        d.setPeriodEnd(e.getPeriodEnd());
        d.setLimitKg(e.getLimitKg());
        return d;
    }

    public static RegionTotalQuota toEntity(RegionTotalQuotaDto d) {
        if (d == null) return null;
        RegionTotalQuota e = new RegionTotalQuota();
        e.setId(d.getId());
        FishingRegion r = new FishingRegion();
        r.setId(d.getRegionId());
        e.setRegion(r);
        e.setPeriodStart(d.getPeriodStart());
        e.setPeriodEnd(d.getPeriodEnd());
        e.setLimitKg(d.getLimitKg());
        return e;
    }
}
