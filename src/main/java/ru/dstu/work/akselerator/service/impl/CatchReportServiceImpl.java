package ru.dstu.work.akselerator.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dstu.work.akselerator.dto.*;
import ru.dstu.work.akselerator.entity.AllocationQuota;
import ru.dstu.work.akselerator.entity.CatchReport;
import ru.dstu.work.akselerator.entity.User;
import ru.dstu.work.akselerator.mapper.CatchReportMapper;
import ru.dstu.work.akselerator.repository.AllocationQuotaRepository;
import ru.dstu.work.akselerator.repository.CatchReportRepository;
import ru.dstu.work.akselerator.repository.UserRepository;
import ru.dstu.work.akselerator.service.CatchReportService;
import java.math.RoundingMode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
public class CatchReportServiceImpl implements CatchReportService {

    private final CatchReportRepository repository;
    private final AllocationQuotaRepository allocationQuotaRepository;
    private final UserRepository userRepository;


    @Autowired
    public CatchReportServiceImpl(CatchReportRepository repository,
                                  AllocationQuotaRepository allocationQuotaRepository,
                                  UserRepository userRepository) {
        this.repository = repository;
        this.allocationQuotaRepository = allocationQuotaRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));
    }

    private WarningInfo calculateQuotaWarning(CatchReport entity) {
        LocalDate fishingDate = entity.getFishingDate();

        Optional<AllocationQuota> quotaOpt = Optional.empty();
        if (entity.getOrganization() != null && entity.getOrganization().getId() != null) {
            quotaOpt = allocationQuotaRepository.findForOrgAndDate(
                    entity.getSpecies().getId(),
                    entity.getRegion().getId(),
                    fishingDate,
                    entity.getOrganization().getId());
        }
        if (quotaOpt.isEmpty()) {
            quotaOpt = allocationQuotaRepository.findGlobalForDate(
                    entity.getSpecies().getId(),
                    entity.getRegion().getId(),
                    fishingDate);
        }

        AllocationQuota quota = quotaOpt.orElse(null);
        if (quota == null) {
            return null;
        }

        BigDecimal usedBefore = repository.sumWeightBySpeciesRegionAndPeriod(
                entity.getSpecies().getId(),
                entity.getRegion().getId(),
                quota.getPeriodStart(),
                quota.getPeriodEnd()
        );
        if (usedBefore == null) usedBefore = BigDecimal.ZERO;

        BigDecimal limit = quota.getLimitKg();
        BigDecimal usedAfter = usedBefore.add(entity.getWeightKg());
        BigDecimal remaining = limit.subtract(usedAfter);
        BigDecimal percent = usedAfter.multiply(BigDecimal.valueOf(100))
                .divide(limit, 2, RoundingMode.HALF_UP);

        if (usedAfter.compareTo(limit) >= 0) {
            return new WarningInfo("ERROR", "Квота превышена", limit, usedAfter, remaining, percent);
        } else if (percent.compareTo(BigDecimal.valueOf(90)) >= 0) {
            return new WarningInfo("WARN", "Квота близка к исчерпанию", limit, usedAfter, remaining, percent);
        }

        return null;
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
    public Page<CatchReport> findLast3ByOrganization(Long organizationId) {
        PageRequest pageable = PageRequest.of(
                0,
                3,
                Sort.by(Sort.Direction.DESC, "fishingDate").and(Sort.by(Sort.Direction.DESC, "id"))
        );
        return repository.findByOrganizationId(organizationId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CatchReport> findByOrganization(Long organizationId, Pageable pageable) {
        return repository.findByOrganizationId(organizationId, pageable);
    }

    @Override
    @Transactional
    public CreateCatchResult verify(Long id) {
        CatchReport report = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CatchReport not found: id=" + id));

        if (report.isVerified()) {
            // уже подтвержден – можно либо просто вернуть, либо бросить исключение
            WarningInfo warning = calculateQuotaWarning(report);
            return new CreateCatchResult(report, warning);
        }

        WarningInfo warning = calculateQuotaWarning(report);

        // тут ты сам решаешь: при "ERROR" запрещать подтверждение или только предупреждать
        // пример: запрещаем, если квота будет превышена
        if (warning != null && "ERROR".equals(warning.getLevel())) {
            throw new ru.dstu.work.akselerator.exception.QuotaExceededException(warning);
        }

        report.setVerified(true);
        CatchReport saved = repository.save(report);

        return new CreateCatchResult(saved, warning);
    }

    @Override
    @Transactional
    public void unverify(Long id) {
        CatchReport report = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CatchReport not found: id=" + id));

        report.setVerified(false);
        repository.save(report);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CatchReport> findMyReports(Pageable pageable) {
        User current = getCurrentUser();
        if (current.getOrganization() == null) {
            return Page.empty(pageable);
        }
        Long orgId = current.getOrganization().getId();
        return repository.findByOrganizationId(orgId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public LastCatchesTableDto getLast3TableForCurrentOrganization() {
        User current = getCurrentUser();

        LastCatchesTableDto table = new LastCatchesTableDto();

        // 9 колонок под все поля CatchReportDto
        table.setColumns(List.of(
                new TableColumnDto("ID отчёта", "id"),
                new TableColumnDto("ID организации", "organizationId"),
                new TableColumnDto("ID пользователя (reportedBy)", "reportedById"),
                new TableColumnDto("ID вида рыбы", "speciesId"),
                new TableColumnDto("ID региона", "regionId"),
                new TableColumnDto("Дата вылова", "fishingDate"),
                new TableColumnDto("Вес, кг", "weightKg"),
                new TableColumnDto("Примечание", "notes"),
                new TableColumnDto("Подтверждён", "verified")
        ));

        if (current.getOrganization() == null) {
            table.setData(List.of());
            return table;
        }

        Long orgId = current.getOrganization().getId();

        PageRequest pageable = PageRequest.of(
                0,
                3,
                Sort.by(Sort.Direction.DESC, "fishingDate")
                        .and(Sort.by(Sort.Direction.DESC, "id"))
        );

        List<CatchReport> reports =
                repository.findByOrganizationId(orgId, pageable).getContent();

        List<CatchReportDto> dtoList = reports.stream()
                .map(CatchReportMapper::toDto)
                .toList();

        table.setData(dtoList);
        return table;
    }


    @Override
    @Transactional(readOnly = true)
    public Page<CatchReport> findPending(Pageable pageable) {
        return repository.findByVerifiedFalse(pageable);
    }


    @Override
    @Transactional(readOnly = true)
    public OrganizationCatchStatsDto getOrganizationStats(Long organizationId) {
        long totalCatches = repository.countByOrganizationId(organizationId);

        BigDecimal totalWeight = repository.sumWeightByOrganization(organizationId);
        if (totalWeight == null) {
            totalWeight = BigDecimal.ZERO;
        }

        YearMonth currentMonth = YearMonth.now();
        LocalDate monthStart = currentMonth.atDay(1);
        LocalDate monthEnd = currentMonth.atEndOfMonth();

        long catchesThisMonth = repository.countByOrganizationIdAndFishingDateBetween(
                organizationId,
                monthStart,
                monthEnd
        );

        Long topRegionId = null;
        String topRegionName = null;

        java.util.List<Object[]> regionStats = repository.findTopRegionByOrganization(organizationId);
        if (!regionStats.isEmpty()) {
            Object[] row = regionStats.get(0);
            topRegionId = (Long) row[0];
            topRegionName = (String) row[1];
        }

        return new OrganizationCatchStatsDto(
                organizationId,
                totalCatches,
                totalWeight,
                catchesThisMonth,
                topRegionId,
                topRegionName
        );
    }


    @Override
    @Transactional
    public CreateCatchResult createCatch(CatchReportDto dto) {
        CatchReport entity = CatchReportMapper.toEntity(dto);

        User current = getCurrentUser();
        if (current.getOrganization() != null) {
            entity.setOrganization(current.getOrganization());
        }

        entity.setVerified(false);

        WarningInfo warning = calculateQuotaWarning(entity);

        CatchReport saved = repository.save(entity);

        return new CreateCatchResult(saved, warning);
    }

}
