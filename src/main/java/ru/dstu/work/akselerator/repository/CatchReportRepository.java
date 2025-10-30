package ru.dstu.work.akselerator.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.dstu.work.akselerator.entity.CatchReport;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface CatchReportRepository extends JpaRepository<CatchReport, Long> {

    Page<CatchReport> findByReportedById(Long reportedById, Pageable pageable);

    Page<CatchReport> findByOrganizationId(Long organizationId, Pageable pageable);

    Page<CatchReport> findByFishingDateBetween(LocalDate start, LocalDate end, Pageable pageable);

    @Query("SELECT COALESCE(SUM(c.weightKg), 0) FROM CatchReport c " +
            "WHERE c.species.id = :speciesId AND c.region.id = :regionId " +
            "AND c.fishingDate BETWEEN :start AND :end")
    BigDecimal sumWeightBySpeciesRegionAndPeriod(
            @Param("speciesId") Long speciesId,
            @Param("regionId") Long regionId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    // Если счёт вести по организации (включая только отчёты той же компании)
    @Query("SELECT COALESCE(SUM(c.weightKg), 0) FROM CatchReport c " +
            "WHERE c.species.id = :speciesId AND c.region.id = :regionId " +
            "AND c.organization.id = :orgId " +
            "AND c.fishingDate BETWEEN :start AND :end")
    BigDecimal sumWeightBySpeciesRegionPeriodForOrg(
            @Param("speciesId") Long speciesId,
            @Param("regionId") Long regionId,
            @Param("orgId") Long orgId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);
}
