package ru.dstu.work.akselerator.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dstu.work.akselerator.dto.DashboardCardsDto;
import ru.dstu.work.akselerator.repository.CatchReportRepository;
import ru.dstu.work.akselerator.repository.FishingRegionRepository;
import ru.dstu.work.akselerator.repository.OrganizationRepository;
import ru.dstu.work.akselerator.service.DashboardStatsService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Year;

@Service
public class DashboardStatsServiceImpl implements DashboardStatsService {

    private final CatchReportRepository catchReportRepository;
    private final OrganizationRepository organizationRepository;
    private final FishingRegionRepository fishingRegionRepository;

    public DashboardStatsServiceImpl(CatchReportRepository catchReportRepository,
                                     OrganizationRepository organizationRepository,
                                     FishingRegionRepository fishingRegionRepository) {
        this.catchReportRepository = catchReportRepository;
        this.organizationRepository = organizationRepository;
        this.fishingRegionRepository = fishingRegionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardCardsDto getCardsStats(Year year) {
        Year targetYear = year != null ? year : Year.now();

        BigDecimal totalCatch = catchReportRepository.sumWeightByYear(targetYear);
        if (totalCatch == null) {
            totalCatch = BigDecimal.ZERO;
        }

        long companies = organizationRepository.count();
        long regions = fishingRegionRepository.count();

        BigDecimal average = BigDecimal.ZERO;
        if (companies > 0) {
            average = totalCatch.divide(BigDecimal.valueOf(companies), 3, RoundingMode.HALF_UP);
        }

        DashboardCardsDto dto = new DashboardCardsDto();
        dto.setYear(targetYear);
        dto.setTotalCatchKg(totalCatch);
        dto.setCompaniesCount(companies);
        dto.setRegionsCount(regions);
        dto.setAverageCatchKg(average);

        return dto;
    }
}
