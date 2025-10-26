package ru.dstu.work.akselerator.mapper;

import ru.dstu.work.akselerator.dto.OrganizationDto;
import ru.dstu.work.akselerator.entity.Organization;
import ru.dstu.work.akselerator.entity.FishingRegion;

public class OrganizationMapper {
    public static OrganizationDto toDto(Organization e) {
        if (e == null) return null;
        Long regionId = e.getRegion() == null ? null : e.getRegion().getId();
        return new OrganizationDto(e.getId(), e.getName(), e.getOrgType(), e.getInn(), regionId);
    }
    public static Organization toEntity(OrganizationDto d) {
        if (d == null) return null;
        Organization e = new Organization();
        e.setName(d.getName());
        e.setOrgType(d.getOrgType());
        e.setInn(d.getInn());
        if (d.getRegionId() != null) {
            FishingRegion r = new FishingRegion();
            r.setId(d.getRegionId());
            e.setRegion(r);
        }
        return e;
    }
}
