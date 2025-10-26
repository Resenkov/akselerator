package ru.dstu.work.akselerator.mapper;

import ru.dstu.work.akselerator.dto.CatchReportDto;
import ru.dstu.work.akselerator.entity.CatchReport;
import ru.dstu.work.akselerator.entity.Organization;
import ru.dstu.work.akselerator.entity.User;
import ru.dstu.work.akselerator.entity.FishSpecies;
import ru.dstu.work.akselerator.entity.FishingRegion;

public class CatchReportMapper {
    public static CatchReportDto toDto(CatchReport e) {
        if (e == null) return null;
        CatchReportDto d = new CatchReportDto();
        d.setId(e.getId());
        d.setOrganizationId(e.getOrganization() == null ? null : e.getOrganization().getId());
        d.setReportedBy(e.getReportedBy() == null ? null : e.getReportedBy().getId());
        d.setSpeciesId(e.getSpecies() == null ? null : e.getSpecies().getId());
        d.setRegionId(e.getRegion() == null ? null : e.getRegion().getId());
        d.setFishingDate(e.getFishingDate());
        d.setWeightKg(e.getWeightKg());
        d.setNotes(e.getNotes());
        d.setVerified(e.isVerified());
        return d;
    }
    public static CatchReport toEntity(CatchReportDto d) {
        if (d == null) return null;
        CatchReport e = new CatchReport();
        if (d.getOrganizationId() != null) { Organization o = new Organization(); o.setId(d.getOrganizationId()); e.setOrganization(o); }
        if (d.getReportedBy() != null) { User u = new User(); u.setId(d.getReportedBy()); e.setReportedBy(u); }
        if (d.getSpeciesId() != null) { FishSpecies s = new FishSpecies(); s.setId(d.getSpeciesId()); e.setSpecies(s); }
        if (d.getRegionId() != null) { FishingRegion r = new FishingRegion(); r.setId(d.getRegionId()); e.setRegion(r); }
        e.setFishingDate(d.getFishingDate());
        e.setWeightKg(d.getWeightKg());
        e.setNotes(d.getNotes());
        e.setVerified(d.isVerified());
        return e;
    }
}
