package ru.dstu.work.akselerator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.dstu.work.akselerator.entity.RegionTotalQuota;

import java.time.LocalDate;
import java.util.List;

public interface RegionTotalQuotaRepository extends JpaRepository<RegionTotalQuota, Long> {

    @Query("SELECT r FROM RegionTotalQuota r " +
            "WHERE r.region.id = :regionId " +
            "AND NOT (r.periodEnd < :periodStart OR r.periodStart > :periodEnd)")
    List<RegionTotalQuota> findOverlapping(@Param("regionId") Long regionId,
                                           @Param("periodStart") LocalDate periodStart,
                                           @Param("periodEnd") LocalDate periodEnd);
}