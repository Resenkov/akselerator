package ru.dstu.work.akselerator.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.dstu.work.akselerator.entity.AllocationQuota;

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

}
