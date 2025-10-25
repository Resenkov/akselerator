package ru.dstu.work.akselerator.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.dstu.work.akselerator.entity.CatchReport;

import java.time.LocalDate;

@Repository
public interface CatchReportRepository extends JpaRepository<CatchReport, Long> {

    Page<CatchReport> findByReportedById(Long reportedById, Pageable pageable);

    Page<CatchReport> findByOrganizationId(Long organizationId, Pageable pageable);

    Page<CatchReport> findByFishingDateBetween(LocalDate start, LocalDate end, Pageable pageable);
}
