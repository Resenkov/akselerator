package ru.dstu.work.akselerator.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.dstu.work.akselerator.entity.AllocationQuota;
import ru.dstu.work.akselerator.entity.FishSpecies;
import ru.dstu.work.akselerator.entity.FishingRegion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AllocationQuotaRepository extends JpaRepository<AllocationQuota, Long> {

    Page<AllocationQuota> findByOrganizationId(Long organizationId, Pageable pageable);

    List<AllocationQuota> findByOrganizationIdAndSpeciesIdAndRegionIdAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqual(
            Long organizationId, Long speciesId, Long regionId, LocalDate date1, LocalDate date2);

    @Query("SELECT q FROM AllocationQuota q WHERE q.species.id = :speciesId AND q.region.id = :regionId " +
            "AND :date BETWEEN q.periodStart AND q.periodEnd AND q.organization.id = :orgId")
    Optional<AllocationQuota> findForOrgAndDate(@Param("speciesId") Long speciesId,
                                                @Param("regionId") Long regionId,
                                                @Param("date") LocalDate date,
                                                @Param("orgId") Long orgId);

    @Query("SELECT q FROM AllocationQuota q WHERE q.species.id = :speciesId AND q.region.id = :regionId " +
            "AND :date BETWEEN q.periodStart AND q.periodEnd AND q.organization IS NULL")
    Optional<AllocationQuota> findGlobalForDate(@Param("speciesId") Long speciesId,
                                                @Param("regionId") Long regionId,
                                                @Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(a.limitKg), 0) FROM AllocationQuota a " +
            "WHERE a.region.id = :regionId " +
            "AND a.species.id = :speciesId " +
            "AND NOT (a.periodEnd < :periodStart OR a.periodStart > :periodEnd) " +
            "AND (:excludeId IS NULL OR a.id <> :excludeId)")
    BigDecimal sumLimitKgByRegionOverlappingPeriod(@Param("regionId") Long regionId,
                                                   @Param("speciesId") Long speciesId,
                                                   @Param("periodStart") LocalDate periodStart,
                                                   @Param("periodEnd") LocalDate periodEnd,
                                                   @Param("excludeId") Long excludeId);

    /**
     * Все уникальные виды рыбы, по которым у организации есть мини-квоты.
     */
    @Query("SELECT DISTINCT q.species FROM AllocationQuota q WHERE q.organization.id = :orgId")
    java.util.List<FishSpecies> findDistinctSpeciesByOrganizationId(@Param("orgId") Long orgId);

    /**
     * Все уникальные регионы, по которым у организации есть мини-квоты.
     */
    @Query("SELECT DISTINCT q.region FROM AllocationQuota q WHERE q.organization.id = :orgId")
    java.util.List<FishingRegion> findDistinctRegionsByOrganizationId(@Param("orgId") Long orgId);

    @Query("SELECT q FROM AllocationQuota q WHERE q.periodStart <= :end AND q.periodEnd >= :start")
    Page<AllocationQuota> findByYear(@Param("start") LocalDate start, @Param("end") LocalDate end, Pageable pageable);
}
