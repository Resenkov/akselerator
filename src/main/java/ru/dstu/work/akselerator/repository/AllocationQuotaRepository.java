package ru.dstu.work.akselerator.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.dstu.work.akselerator.entity.AllocationQuota;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AllocationQuotaRepository extends JpaRepository<AllocationQuota, Long> {

    Page<AllocationQuota> findByOrganizationId(Long organizationId, Pageable pageable);

    List<AllocationQuota> findByOrganizationIdAndSpeciesIdAndRegionIdAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqual(
            Long organizationId, Long speciesId, Long regionId, LocalDate date1, LocalDate date2);

}
