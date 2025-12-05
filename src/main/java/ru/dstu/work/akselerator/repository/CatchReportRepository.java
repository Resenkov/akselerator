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
import java.time.Year;

@Repository
public interface CatchReportRepository extends JpaRepository<CatchReport, Long> {

    Page<CatchReport> findByOrganizationId(Long organizationId, Pageable pageable);

    Page<CatchReport> findByVerifiedFalse(Pageable pageable);

    @Query("SELECT COALESCE(SUM(c.weightKg), 0) FROM CatchReport c " +
            "WHERE c.species.id = :speciesId " +
            "AND c.region.id = :regionId " +
            "AND c.fishingDate BETWEEN :start AND :end")
    BigDecimal sumWeightBySpeciesRegionAndPeriod(
            @Param("speciesId") Long speciesId,
            @Param("regionId") Long regionId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT COALESCE(SUM(c.weightKg), 0) FROM CatchReport c " +
            "WHERE c.species.id = :speciesId AND c.region.id = :regionId " +
            "AND c.organization.id = :orgId " +
            "AND c.fishingDate BETWEEN :start AND :end")
    BigDecimal sumWeightBySpeciesRegionPeriodForOrg(
            @Param("speciesId") Long speciesId,
            @Param("regionId") Long regionId,
            @Param("orgId") Long orgId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    @Query("""
       SELECT COALESCE(SUM(c.weightKg), 0)
       FROM CatchReport c
       WHERE c.fishingDate BETWEEN :start AND :end
       """)
    BigDecimal sumWeightByYear(
            @Param("start") java.time.LocalDate start,
            @Param("end") java.time.LocalDate end
    );

    @Query("SELECT COALESCE(SUM(c.weightKg), 0) FROM CatchReport c")
    BigDecimal sumWeightAllTime();




    long countByOrganizationId(Long organizationId);

    @Query("SELECT COALESCE(SUM(c.weightKg), 0) FROM CatchReport c WHERE c.organization.id = :orgId")
    BigDecimal sumWeightByOrganization(@Param("orgId") Long organizationId);

    long countByOrganizationIdAndFishingDateBetween(Long organizationId, LocalDate start, LocalDate end);

    @Query("""
           SELECT c.region.id, c.region.name, COUNT(c) as cnt 
           FROM CatchReport c 
           WHERE c.organization.id = :orgId 
           GROUP BY c.region.id, c.region.name 
           ORDER BY cnt DESC
           """)
    java.util.List<Object[]> findTopRegionByOrganization(@Param("orgId") Long organizationId);
}
