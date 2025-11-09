package ru.dstu.work.akselerator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.dstu.work.akselerator.entity.RegionalQuota;

import java.time.LocalDate;
import java.util.List;

public interface RegionalQuotaRepository extends JpaRepository<RegionalQuota, Long> {

    @Query("SELECT r FROM RegionalQuota r " +
           "WHERE r.species.id = :speciesId AND r.region.id = :regionId " +
           "AND NOT (r.periodEnd < :periodStart OR r.periodStart > :periodEnd)")
    List<RegionalQuota> findOverlapping(@Param("speciesId") Long speciesId,
                                        @Param("regionId") Long regionId,
                                        @Param("periodStart") LocalDate periodStart,
                                        @Param("periodEnd") LocalDate periodEnd);
}
