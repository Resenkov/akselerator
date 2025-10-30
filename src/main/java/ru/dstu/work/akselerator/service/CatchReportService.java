package ru.dstu.work.akselerator.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.dstu.work.akselerator.dto.CatchReportDto;
import ru.dstu.work.akselerator.dto.CreateCatchResult;
import ru.dstu.work.akselerator.entity.CatchReport;

import java.time.LocalDate;
import java.util.Optional;

public interface CatchReportService {
    CatchReport create(CatchReport report);
    Optional<CatchReport> getById(Long id);
    Page<CatchReport> list(Pageable pageable);
    CatchReport update(CatchReport report);
    void deleteById(Long id);
    CreateCatchResult createCatch(CatchReportDto dto);

    Page<CatchReport> findByReportedBy(Long reportedById, Pageable pageable);
    Page<CatchReport> findByOrganization(Long organizationId, Pageable pageable);
    Page<CatchReport> findByFishingDateBetween(LocalDate start, LocalDate end, Pageable pageable);
}
