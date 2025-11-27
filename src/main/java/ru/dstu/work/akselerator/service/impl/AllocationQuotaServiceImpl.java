package ru.dstu.work.akselerator.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dstu.work.akselerator.dto.*;
import ru.dstu.work.akselerator.entity.*;
import ru.dstu.work.akselerator.exception.QuotaExceededException;
import ru.dstu.work.akselerator.mapper.FishSpeciesMapper;
import ru.dstu.work.akselerator.mapper.FishingRegionMapper;
import ru.dstu.work.akselerator.repository.AllocationQuotaRepository;
import ru.dstu.work.akselerator.repository.RegionTotalQuotaRepository;
import ru.dstu.work.akselerator.repository.UserRepository;
import ru.dstu.work.akselerator.service.AllocationQuotaService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AllocationQuotaServiceImpl implements AllocationQuotaService {

    private final AllocationQuotaRepository repository;
    private final RegionTotalQuotaRepository regionTotalQuotaRepository;
    private final UserRepository userRepository;

    @Autowired
    public AllocationQuotaServiceImpl(AllocationQuotaRepository repository,
                                      RegionTotalQuotaRepository regionTotalQuotaRepository, UserRepository userRepository) {
        this.repository = repository;
        this.regionTotalQuotaRepository = regionTotalQuotaRepository;
        this.userRepository = userRepository;
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


    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public AvailableSpeciesAndRegionsDto getAvailableSpeciesAndRegionsForCurrentOrg() {
        User current = getCurrentUser();

        AvailableSpeciesAndRegionsDto resp = new AvailableSpeciesAndRegionsDto();

        if (current.getOrganization() == null) {
            resp.setSpecies(List.of());
            resp.setRegions(List.of());
            return resp;
        }

        Long orgId = current.getOrganization().getId();

        var quotasPage = repository.findByOrganizationId(orgId, Pageable.unpaged());
        var quotas = quotasPage.getContent();

        Map<Long, FishSpecies> speciesMap = new HashMap<>();
        Map<Long, FishingRegion> regionMap = new HashMap<>();

        for (AllocationQuota q : quotas) {
            if (q.getSpecies() != null) {
                speciesMap.putIfAbsent(q.getSpecies().getId(), q.getSpecies());
            }
            if (q.getRegion() != null) {
                regionMap.putIfAbsent(q.getRegion().getId(), q.getRegion());
            }
        }

        List<FishSpeciesDto> speciesDtos = speciesMap.values().stream()
                .map(FishSpeciesMapper::toDto)
                .toList();

        List<FishingRegionDto> regionDtos = regionMap.values().stream()
                .map(FishingRegionMapper::toDto)
                .toList();

        resp.setSpecies(speciesDtos);
        resp.setRegions(regionDtos);
        return resp;
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
