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
import ru.dstu.work.akselerator.mapper.AllocationQuotaMapper;
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


    private AllocationQuotaDto toDtoWithUsage(AllocationQuota q) {
        AllocationQuotaDto dto = AllocationQuotaMapper.toDto(q);
        dto.setUsedKg(calculateUsedKg(q));
        return dto;
    }


    @Override
    @Transactional(readOnly = true)
    public AllocationQuotaDto getDtoWithUsage(Long id) {
        AllocationQuota q = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quota not found: id=" + id));

        return enrichDtoWithUsage(q);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AllocationQuotaDto> listDtosWithUsage(Integer year, Pageable pageable) {
        Page<AllocationQuota> page;

        if (year == null) {
            page = repository.findAll(pageable);
        } else {
            LocalDate start = LocalDate.of(year, 1, 1);
            LocalDate end = LocalDate.of(year, 12, 31);
            page = repository.findByYear(start, end, pageable);
        }

        return page.map(this::enrichDtoWithUsage);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AllocationQuotaDto> listDtosWithUsageByOrganization(Long orgId, Pageable pageable) {
        Page<AllocationQuota> page = repository.findByOrganizationId(orgId, pageable);
        return page.map(this::enrichDtoWithUsage);
    }

    private BigDecimal calculateUsedKg(AllocationQuota q) {
        if (q == null || q.getOrganization() == null
                || q.getSpecies() == null || q.getRegion() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal used = catchReportRepository.sumWeightBySpeciesRegionPeriodForOrg(
                q.getSpecies().getId(),
                q.getRegion().getId(),
                q.getOrganization().getId(),
                q.getPeriodStart(),
                q.getPeriodEnd()
        );
        if (used == null || used.compareTo(BigDecimal.ZERO) == 0) {
            used = catchReportRepository.sumWeightBySpeciesRegionForOrg(
                    q.getSpecies().getId(),
                    q.getRegion().getId(),
                    q.getOrganization().getId()
            );
        }

        return used != null ? used : BigDecimal.ZERO;
    }

    private AllocationQuotaDto enrichDtoWithUsage(AllocationQuota q) {
        AllocationQuotaDto dto = AllocationQuotaMapper.toDto(q);
        dto.setUsedKg(calculateUsedKg(q));
        return dto;
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
    public Page<AllocationQuotaDto> findDtosByOrganizationWithUsage(Long organizationId, Pageable pageable) {
        Page<AllocationQuota> page = repository.findByOrganizationId(organizationId, pageable);
        return page.map(this::enrichDtoWithUsage);
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

        return mapUsageDtos(quotas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuotaUsageSummaryDto> getQuotaUsageSummary(Long organizationId) {
        Page<AllocationQuota> quotasPage;
        if (organizationId != null) {
            quotasPage = repository.findByOrganizationId(organizationId, Pageable.unpaged());
        } else {
            quotasPage = repository.findAll(Pageable.unpaged());
        }

        return mapUsageDtos(quotasPage.getContent());
    }

    private List<QuotaUsageSummaryDto> mapUsageDtos(List<AllocationQuota> quotas) {
        return quotas.stream()
                .map(q -> {
                    QuotaUsageSummaryDto dto = new QuotaUsageSummaryDto();
                    dto.setQuotaId(q.getId());

                    if (q.getOrganization() != null) {
                        dto.setOrganizationId(q.getOrganization().getId());
                        dto.setOrganizationName(q.getOrganization().getName());
                    }

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

                    BigDecimal used = BigDecimal.ZERO;
                    if (q.getOrganization() != null && q.getSpecies() != null && q.getRegion() != null) {
                        used = catchReportRepository.sumWeightBySpeciesRegionPeriodForOrg(
                                q.getSpecies().getId(),
                                q.getRegion().getId(),
                                q.getOrganization().getId(),
                                q.getPeriodStart(),
                                q.getPeriodEnd()
                        );

                        if (used == null || used.compareTo(BigDecimal.ZERO) == 0) {
                            used = catchReportRepository.sumWeightBySpeciesRegionForOrg(
                                    q.getSpecies().getId(),
                                    q.getRegion().getId(),
                                    q.getOrganization().getId()
                            );
                        }
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
        return buildTable(page);
    }

    @Override
    public AllocationQuotasTableDto listAsTableForOrganization(Long organizationId, Pageable pageable) {
        Page<AllocationQuota> page = repository.findByOrganizationId(organizationId, pageable);
        return buildTable(page);
    }

    private AllocationQuotasTableDto buildTable(Page<AllocationQuota> page) {
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
                    row.setUsedKg(calculateUsedKg(q));

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

        // 0. sanity check по датам
        if (q.getPeriodEnd().isBefore(q.getPeriodStart())) {
            throw new IllegalArgumentException("Конец периода мини-квоты не может быть раньше начала");
        }

        // 1. Находим региональные квоты, пересекающиеся по датам
        var overlappingTotals = regionTotalQuotaRepository.findOverlapping(
                regionId, speciesId, q.getPeriodStart(), q.getPeriodEnd()
        );

        if (overlappingTotals.isEmpty()) {
            // нет вообще никакой региональной квоты для этого вида/региона и периода
            WarningInfo w = new WarningInfo();
            w.setLevel("ERROR");
            w.setMessage("Для указанного региона и вида рыбы не задана общая региональная квота " +
                    "или период мини-квоты полностью выходит за рамки региональной квоты.");
            w.setQuotaLimitKg(BigDecimal.ZERO);
            w.setUsedKg(q.getLimitKg());
            w.setRemainingKg(BigDecimal.ZERO);
            throw new QuotaExceededException(w);
        }

        // 2. Берём региональную квоту, которая ПОЛНОСТЬЮ покрывает период мини-квоты
        RegionTotalQuota rt = overlappingTotals.stream()
                .filter(r -> !r.getPeriodStart().isAfter(q.getPeriodStart())   // r.start <= q.start
                        && !r.getPeriodEnd().isBefore(q.getPeriodEnd()))    // r.end   >= q.end
                .findFirst()
                .orElseThrow(() -> {
                    WarningInfo w = new WarningInfo();
                    w.setLevel("ERROR");
                    w.setMessage("Период мини-квоты должен полностью находиться внутри периода общей " +
                            "региональной квоты.");
                    w.setQuotaLimitKg(BigDecimal.ZERO);
                    w.setUsedKg(q.getLimitKg());
                    w.setRemainingKg(BigDecimal.ZERO);
                    return new QuotaExceededException(w);
                });

        // 3. Считаем, сколько уже выдано мини-квот по этому региону/виду в рамках региональной квоты
        BigDecimal already = repository.sumLimitKgByRegionOverlappingPeriod(
                regionId,
                speciesId,
                rt.getPeriodStart(),
                rt.getPeriodEnd(),
                excludeId
        );
        if (already == null) {
            already = BigDecimal.ZERO;
        }

        BigDecimal used = already.add(q.getLimitKg());

        // 4. Проверяем, что сумма мини-квот (включая текущую) не превышает региональный лимит
        if (used.compareTo(rt.getLimitKg()) > 0) {
            WarningInfo w = new WarningInfo();
            w.setLevel("ERROR");
            w.setMessage("Сумма мини-квот по региону и виду рыбы превышает общий лимит региональной квоты.");
            w.setQuotaLimitKg(rt.getLimitKg());
            w.setUsedKg(used);
            w.setRemainingKg(rt.getLimitKg().subtract(used));
            if (rt.getLimitKg().signum() > 0) {
                w.setPercentUsed(
                        used.multiply(BigDecimal.valueOf(100))
                                .divide(rt.getLimitKg(), 4, RoundingMode.HALF_UP)
                );
            }
            throw new QuotaExceededException(w);
        }
    }
}