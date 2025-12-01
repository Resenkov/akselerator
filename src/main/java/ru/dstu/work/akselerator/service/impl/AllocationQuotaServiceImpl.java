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
import ru.dstu.work.akselerator.repository.CatchReportRepository;
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
    private final CatchReportRepository catchReportRepository;

    @Autowired
    public AllocationQuotaServiceImpl(AllocationQuotaRepository repository,
                                      RegionTotalQuotaRepository regionTotalQuotaRepository, UserRepository userRepository, CatchReportRepository catchReportRepository) {
        this.repository = repository;
        this.regionTotalQuotaRepository = regionTotalQuotaRepository;
        this.userRepository = userRepository;
        this.catchReportRepository = catchReportRepository;
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
    public List<QuotaUsageSummaryDto> getMyQuotaUsageSummary() {
        User current = getCurrentUser();

        if (current.getOrganization() == null) {
            return List.of();
        }

        Long orgId = current.getOrganization().getId();

        var quotasPage = repository.findByOrganizationId(orgId, Pageable.unpaged());
        var quotas = quotasPage.getContent();

        return quotas.stream()
                .map(q -> {
                    QuotaUsageSummaryDto dto = new QuotaUsageSummaryDto();
                    dto.setQuotaId(q.getId());

                    if (q.getSpecies() != null) {
                        dto.setSpeciesId(q.getSpecies().getId());
                        dto.setSpeciesCommonName(q.getSpecies().getCommonName());
                        dto.setSpeciesScientificName(q.getSpecies().getScientificName());
                    }

                    if (q.getRegion() != null) {
                        dto.setRegionId(q.getRegion().getId());
                        dto.setRegionName(q.getRegion().getName());
                        dto.setRegionCode(q.getRegion().getCode());
                    }

                    dto.setPeriodStart(q.getPeriodStart());
                    dto.setPeriodEnd(q.getPeriodEnd());
                    dto.setLimitKg(q.getLimitKg());

                    // считаем, сколько уже выловлено по этой квоте
                    BigDecimal used = BigDecimal.ZERO;
                    if (q.getOrganization() != null && q.getSpecies() != null && q.getRegion() != null) {
                        used = catchReportRepository.sumWeightBySpeciesRegionPeriodForOrg(
                                q.getSpecies().getId(),
                                q.getRegion().getId(),
                                q.getOrganization().getId(),
                                q.getPeriodStart(),
                                q.getPeriodEnd()
                        );
                    }
                    if (used == null) used = BigDecimal.ZERO;
                    dto.setUsedKg(used);

                    return dto;
                })
                .toList();
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
    public AllocationQuotasTableDto listAsTable(Pageable pageable) {
        Page<AllocationQuota> page = repository.findAll(pageable);

        AllocationQuotasTableDto table = new AllocationQuotasTableDto();

        table.setColumns(List.of(
                new TableColumnDto("ID квоты", "id"),
                new TableColumnDto("Организация (ID)", "organizationId"),
                new TableColumnDto("Организация", "organizationName"),
                new TableColumnDto("Вид рыбы (ID)", "speciesId"),
                new TableColumnDto("Вид рыбы (рус.)", "speciesCommonName"),
                new TableColumnDto("Вид рыбы (лат.)", "speciesScientificName"),
                new TableColumnDto("Регион (ID)", "regionId"),
                new TableColumnDto("Регион", "regionName"),
                new TableColumnDto("Код региона", "regionCode"),
                new TableColumnDto("Начало периода", "periodStart"),
                new TableColumnDto("Конец периода", "periodEnd"),
                new TableColumnDto("Лимит, кг", "limitKg"),
                // НОВАЯ колонка
                new TableColumnDto("Поймано, кг", "usedKg")
        ));

        var rows = page.getContent().stream()
                .map(q -> {
                    AllocationQuotaTableRowDto row = new AllocationQuotaTableRowDto();
                    row.setId(q.getId());

                    if (q.getOrganization() != null) {
                        row.setOrganizationId(q.getOrganization().getId());
                        row.setOrganizationName(q.getOrganization().getName());
                    }

                    if (q.getSpecies() != null) {
                        row.setSpeciesId(q.getSpecies().getId());
                        row.setSpeciesCommonName(q.getSpecies().getCommonName());
                        row.setSpeciesScientificName(q.getSpecies().getScientificName());
                    }

                    if (q.getRegion() != null) {
                        row.setRegionId(q.getRegion().getId());
                        row.setRegionName(q.getRegion().getName());
                        row.setRegionCode(q.getRegion().getCode());
                    }

                    row.setPeriodStart(q.getPeriodStart());
                    row.setPeriodEnd(q.getPeriodEnd());
                    row.setLimitKg(q.getLimitKg());

                    // === НОВОЕ: считаем пойманное по этой квоте ===
                    BigDecimal used = BigDecimal.ZERO;
                    if (q.getOrganization() != null && q.getSpecies() != null && q.getRegion() != null) {
                        used = catchReportRepository.sumWeightBySpeciesRegionPeriodForOrg(
                                q.getSpecies().getId(),
                                q.getRegion().getId(),
                                q.getOrganization().getId(),
                                q.getPeriodStart(),
                                q.getPeriodEnd()
                        );
                    }
                    if (used == null) {
                        used = BigDecimal.ZERO;
                    }
                    row.setUsedKg(used);

                    return row;
                })
                .toList();

        table.setData(rows);
        return table;
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
        Long speciesId = q.getSpecies().getId();
        List<RegionTotalQuota> totals = regionTotalQuotaRepository.findOverlapping(
                regionId, speciesId, q.getPeriodStart(), q.getPeriodEnd()
        );
        if (totals.isEmpty()) return;

        RegionTotalQuota rt = totals.get(0);
        BigDecimal already = repository.sumLimitKgByRegionOverlappingPeriod(
                regionId, speciesId, q.getPeriodStart(), q.getPeriodEnd(), excludeId
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
