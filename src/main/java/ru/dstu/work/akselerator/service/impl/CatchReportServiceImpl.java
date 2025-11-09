package ru.dstu.work.akselerator.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dstu.work.akselerator.dto.CatchReportDto;
import ru.dstu.work.akselerator.dto.CreateCatchResult;
import ru.dstu.work.akselerator.dto.WarningInfo;
import ru.dstu.work.akselerator.entity.AllocationQuota;
import ru.dstu.work.akselerator.entity.CatchReport;
import ru.dstu.work.akselerator.mapper.CatchReportMapper;
import ru.dstu.work.akselerator.repository.AllocationQuotaRepository;
import ru.dstu.work.akselerator.repository.CatchReportRepository;
import ru.dstu.work.akselerator.service.CatchReportService;
import java.math.RoundingMode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class CatchReportServiceImpl implements CatchReportService {

    private final CatchReportRepository repository;
    private final AllocationQuotaRepository allocationQuotaRepository;

    @Autowired
    public CatchReportServiceImpl(CatchReportRepository repository, AllocationQuotaRepository allocationQuotaRepository) {
        this.repository = repository;
        this.allocationQuotaRepository = allocationQuotaRepository;
    }

    @Override
    @Transactional
    public CatchReport create(CatchReport report) {
        return repository.save(report);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CatchReport> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CatchReport> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    @Transactional
    public CatchReport update(CatchReport report) {
        return repository.save(report);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CatchReport> findByReportedBy(Long reportedById, Pageable pageable) {
        return repository.findByReportedById(reportedById, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CatchReport> findByOrganization(Long organizationId, Pageable pageable) {
        return repository.findByOrganizationId(organizationId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CatchReport> findByFishingDateBetween(LocalDate start, LocalDate end, Pageable pageable) {
        return repository.findByFishingDateBetween(start, end, pageable);
    }


    @Override
    @Transactional
    public CreateCatchResult createCatch(CatchReportDto dto) {
        CatchReport entity = CatchReportMapper.toEntity(dto);

        LocalDate fishingDate = entity.getFishingDate();

        Optional<AllocationQuota> quotaOpt = Optional.empty();
        if (entity.getOrganization() != null && entity.getOrganization().getId() != null) {
            quotaOpt = allocationQuotaRepository.findForOrgAndDate(entity.getSpecies().getId(),
                    entity.getRegion().getId(),
                    fishingDate,
                    entity.getOrganization().getId());
        }
        if (quotaOpt.isEmpty()) {
            quotaOpt = allocationQuotaRepository.findGlobalForDate(entity.getSpecies().getId(),
                    entity.getRegion().getId(),
                    fishingDate);
        }

        AllocationQuota quota = quotaOpt.orElse(null);

        BigDecimal usedBefore = BigDecimal.ZERO;
        if (quota != null) {
            usedBefore = repository.sumWeightBySpeciesRegionAndPeriod(
                    entity.getSpecies().getId(),
                    entity.getRegion().getId(),
                    quota.getPeriodStart(),
                    quota.getPeriodEnd()
            );
        }

        CatchReport saved = repository.save(entity);

        WarningInfo warning = null;
        if (quota != null) {
            BigDecimal limit = quota.getLimitKg();
            BigDecimal usedAfter = usedBefore.add(entity.getWeightKg());
            BigDecimal remaining = limit.subtract(usedAfter);
            BigDecimal percent = usedAfter.multiply(BigDecimal.valueOf(100))
                    .divide(limit, 2, RoundingMode.HALF_UP);

            if (usedAfter.compareTo(limit) >= 0) {
                warning = new WarningInfo("ERROR", "Квота превышена", limit, usedAfter, remaining, percent);
            } else if (percent.compareTo(BigDecimal.valueOf(90)) >= 0) {
                warning = new WarningInfo("WARN", "Квота близка к исчерпанию", limit, usedAfter, remaining, percent);
            }
        }

        return new CreateCatchResult(saved, warning);
    }
}
