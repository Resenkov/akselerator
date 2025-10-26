package ru.dstu.work.akselerator.mapper;

import ru.dstu.work.akselerator.dto.AllocationQuotaDto;
import ru.dstu.work.akselerator.entity.AllocationQuota;
import ru.dstu.work.akselerator.entity.Organization;
import ru.dstu.work.akselerator.entity.FishSpecies;
import ru.dstu.work.akselerator.entity.FishingRegion;

public class AllocationQuotaMapper {
    public static AllocationQuotaDto toDto(AllocationQuota e) {
        if (e == null) return null;
        AllocationQuotaDto d = new AllocationQuotaDto();
        d.setId(e.getId());
        d.setOrganizationId(e.getOrganization() == null ? null : e.getOrganization().getId());
        d.setSpeciesId(e.getSpecies() == null ? null : e.getSpecies().getId());
        d.setRegionId(e.getRegion() == null ? null : e.getRegion().getId());
        d.setPeriodStart(e.getPeriodStart());
        d.setPeriodEnd(e.getPeriodEnd());
        d.setLimitKg(e.getLimitKg());
        return d;
    }
    public static AllocationQuota toEntity(AllocationQuotaDto d) {
        if (d == null) return null;
        AllocationQuota e = new AllocationQuota();
        if (d.getOrganizationId() != null) { Organization o = new Organization(); o.setId(d.getOrganizationId()); e.setOrganization(o); }
        if (d.getSpeciesId() != null) { FishSpecies s = new FishSpecies(); s.setId(d.getSpeciesId()); e.setSpecies(s); }
        if (d.getRegionId() != null) { FishingRegion r = new FishingRegion(); r.setId(d.getRegionId()); e.setRegion(r); }
        e.setPeriodStart(d.getPeriodStart());
        e.setPeriodEnd(d.getPeriodEnd());
        e.setLimitKg(d.getLimitKg());
        return e;
    }
}
