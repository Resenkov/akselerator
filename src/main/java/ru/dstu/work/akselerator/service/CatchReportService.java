package ru.dstu.work.akselerator.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.dstu.work.akselerator.dto.CatchReportDto;
import ru.dstu.work.akselerator.dto.CreateCatchResult;
import ru.dstu.work.akselerator.dto.LastCatchesTableDto;
import ru.dstu.work.akselerator.dto.OrganizationCatchStatsDto;
import ru.dstu.work.akselerator.entity.CatchReport;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CatchReportService {
    CatchReport create(CatchReport report);
    Optional<CatchReport> getById(Long id);
    Page<CatchReport> list(Pageable pageable);
    CatchReport update(CatchReport report);
    void deleteById(Long id);
    CreateCatchResult createCatch(CatchReportDto dto);
    Page<CatchReport> findLast3ByOrganization(Long organizationId);
    OrganizationCatchStatsDto getOrganizationStats(Long organizationId);
    Page<CatchReport> findByOrganization(Long organizationId, Pageable pageable);
    CreateCatchResult verify(Long id);
    void unverify(Long id);
    Page<CatchReport> findMyReports(Pageable pageable);
    LastCatchesTableDto getLast3TableForCurrentOrganization();

    Page<CatchReport> findPending(Pageable pageable);

}