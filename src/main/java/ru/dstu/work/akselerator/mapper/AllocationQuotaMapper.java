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
        if (e.getOrganization() != null) {
            d.setOrganizationId(e.getOrganization().getId());
            d.setOrganizationName(e.getOrganization().getName());
        }
        if (e.getSpecies() != null) {
            d.setSpeciesId(e.getSpecies().getId());
            d.setSpeciesCommonName(e.getSpecies().getCommonName());
            d.setSpeciesScientificName(e.getSpecies().getScientificName());
        }
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
