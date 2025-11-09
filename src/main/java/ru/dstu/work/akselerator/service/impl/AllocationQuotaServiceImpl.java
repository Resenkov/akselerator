package ru.dstu.work.akselerator.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dstu.work.akselerator.dto.WarningInfo;
import ru.dstu.work.akselerator.entity.AllocationQuota;
import ru.dstu.work.akselerator.entity.RegionTotalQuota;
import ru.dstu.work.akselerator.exception.QuotaExceededException;
import ru.dstu.work.akselerator.repository.AllocationQuotaRepository;
import ru.dstu.work.akselerator.repository.RegionTotalQuotaRepository;
import ru.dstu.work.akselerator.service.AllocationQuotaService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AllocationQuotaServiceImpl implements AllocationQuotaService {

    private final AllocationQuotaRepository repository;
    private final RegionTotalQuotaRepository regionTotalQuotaRepository;

    @Autowired
    public AllocationQuotaServiceImpl(AllocationQuotaRepository repository,
                                      RegionTotalQuotaRepository regionTotalQuotaRepository) {
        this.repository = repository;
        this.regionTotalQuotaRepository = regionTotalQuotaRepository;
    }

    @Override
    @Transactional
    public AllocationQuota create(AllocationQuota quota) {
        validateAgainstRegionTotal(quota, null);
        return repository.save(quota);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AllocationQuota> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AllocationQuota> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    @Transactional
    public AllocationQuota update(AllocationQuota quota) {
        validateAgainstRegionTotal(quota, quota.getId());
        return repository.save(quota);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AllocationQuota> findByOrganizationId(Long organizationId, Pageable pageable) {
        return repository.findByOrganizationId(organizationId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AllocationQuota> findActiveFor(Long organizationId, Long speciesId, Long regionId, LocalDate date) {
        return repository.findByOrganizationIdAndSpeciesIdAndRegionIdAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqual(
                organizationId, speciesId, regionId, date, date
        );
    }

    private void validateAgainstRegionTotal(AllocationQuota q, Long excludeId) {
        Long regionId = q.getRegion().getId();
        List<RegionTotalQuota> totals = regionTotalQuotaRepository.findOverlapping(
                regionId, q.getPeriodStart(), q.getPeriodEnd()
        );
        if (totals.isEmpty()) return;

        RegionTotalQuota rt = totals.get(0);
        BigDecimal already = repository.sumLimitKgByRegionOverlappingPeriod(
                regionId, q.getPeriodStart(), q.getPeriodEnd(), excludeId
        );
        if (already == null) already = BigDecimal.ZERO;

        BigDecimal used = already.add(q.getLimitKg());
        if (used.compareTo(rt.getLimitKg()) > 0) {
            WarningInfo w = new WarningInfo();
            w.setLevel("ERROR");
            w.setMessage("Сумма мини-квот по региону превышает общий лимит региона");
            w.setQuotaLimitKg(rt.getLimitKg());
            w.setUsedKg(used);
            w.setRemainingKg(rt.getLimitKg().subtract(used));
            if (rt.getLimitKg().signum() > 0) {
                w.setPercentUsed(used.multiply(BigDecimal.valueOf(100))
                        .divide(rt.getLimitKg(), 4, RoundingMode.HALF_UP));
            }
            throw new QuotaExceededException(w);
        }
    }
}
