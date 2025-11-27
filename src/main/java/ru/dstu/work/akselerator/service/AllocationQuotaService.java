package ru.dstu.work.akselerator.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.dstu.work.akselerator.dto.AllocationQuotasTableDto;
import ru.dstu.work.akselerator.dto.AvailableSpeciesAndRegionsDto;
import ru.dstu.work.akselerator.dto.QuotaUsageSummaryDto;
import ru.dstu.work.akselerator.entity.AllocationQuota;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AllocationQuotaService {
    AllocationQuota create(AllocationQuota quota);
    Optional<AllocationQuota> getById(Long id);
    Page<AllocationQuota> list(Pageable pageable);
    AllocationQuota update(AllocationQuota quota);
    void deleteById(Long id);

    Page<AllocationQuota> findByOrganizationId(Long organizationId, Pageable pageable);
    List<QuotaUsageSummaryDto> getMyQuotaUsageSummary();

    List<AllocationQuota> findActiveFor(Long organizationId, Long speciesId, Long regionId, LocalDate date);
    AvailableSpeciesAndRegionsDto getAvailableSpeciesAndRegionsForCurrentOrg();
    AllocationQuotasTableDto listAsTable(Pageable pageable);

}
