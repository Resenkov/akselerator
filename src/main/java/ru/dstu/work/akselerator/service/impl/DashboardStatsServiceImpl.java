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
import java.time.LocalDate;

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
    public DashboardCardsDto getCardsStats(LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();

        BigDecimal totalCatch = catchReportRepository.sumWeightByDate(targetDate);
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
        dto.setDate(targetDate);
        dto.setTotalCatchKg(totalCatch);
        dto.setCompaniesCount(companies);
        dto.setRegionsCount(regions);
        dto.setAverageCatchKg(average);

        return dto;
    }
}
