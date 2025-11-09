package ru.dstu.work.akselerator.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.dstu.work.akselerator.entity.AllocationQuota;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface AllocationQuotaRepository extends JpaRepository<AllocationQuota, Long> {

    @Query("""
           SELECT COALESCE(SUM(a.limitKg), 0)
           FROM AllocationQuota a
           WHERE a.species.id = :speciesId
             AND a.region.id  = :regionId
             AND NOT (a.periodEnd < :periodStart OR a.periodStart > :periodEnd)
             AND (:excludeId IS NULL OR a.id <> :excludeId)
           """)
    BigDecimal sumLimitKgBySpeciesRegionOverlappingPeriod(@Param("speciesId") Long speciesId,
                                                          @Param("regionId") Long regionId,
                                                          @Param("periodStart") LocalDate periodStart,
                                                          @Param("periodEnd") LocalDate periodEnd,
                                                          @Param("excludeId") Long excludeId);

    @Query("""
           SELECT a FROM AllocationQuota a
            WHERE a.species.id = :speciesId
              AND a.region.id = :regionId
              AND a.organization.id = :orgId
              AND :date BETWEEN a.periodStart AND a.periodEnd
           """)
    Optional<AllocationQuota> findForOrgAndDate(@Param("speciesId") Long speciesId,
                                                @Param("regionId") Long regionId,
                                                @Param("date") LocalDate date,
                                                @Param("orgId") Long orgId);

    @Query("""
           SELECT a FROM AllocationQuota a
            WHERE a.species.id = :speciesId
              AND a.region.id = :regionId
              AND a.organization IS NULL
              AND :date BETWEEN a.periodStart AND a.periodEnd
           """)
    Optional<AllocationQuota> findGlobalForDate(@Param("speciesId") Long speciesId,
                                                @Param("regionId") Long regionId,
                                                @Param("date") LocalDate date);

    Page<AllocationQuota> findByOrganizationId(Long organizationId, Pageable pageable);
}
