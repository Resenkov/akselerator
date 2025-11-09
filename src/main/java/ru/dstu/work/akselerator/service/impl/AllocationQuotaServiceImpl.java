package ru.dstu.work.akselerator.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dstu.work.akselerator.entity.AllocationQuota;
import ru.dstu.work.akselerator.repository.AllocationQuotaRepository;
import ru.dstu.work.akselerator.service.AllocationQuotaService;
import ru.dstu.work.akselerator.service.support.QuotaGuard;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AllocationQuotaServiceImpl implements AllocationQuotaService {

    private final AllocationQuotaRepository repository;
    private final QuotaGuard quotaGuard;

    public AllocationQuotaServiceImpl(AllocationQuotaRepository repository, QuotaGuard quotaGuard) {
        this.repository = repository;
        this.quotaGuard = quotaGuard;
    }

    // === Методы ИНТЕРФЕЙСА ===

    @Override
    public AllocationQuota create(AllocationQuota quota) {
        checkGuard(quota, null);
        return repository.save(quota);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AllocationQuota> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AllocationQuota> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public AllocationQuota update(AllocationQuota quota) {
        Long id = quota.getId();
        if (id == null || !repository.existsById(id)) {
            throw new IllegalArgumentException("Allocation quota not found: " + id);
        }
        checkGuard(quota, id);
        return repository.save(quota);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    // === Внутренняя проверка превышения региональной квоты ===
    private void checkGuard(AllocationQuota q, Long excludeId) {
        Long speciesId = (q.getSpecies() != null) ? q.getSpecies().getId() : null;
        Long regionId  = (q.getRegion()  != null) ? q.getRegion().getId()  : null;

        quotaGuard.checkAllocationAgainstRegional(
                speciesId,
                regionId,
                q.getPeriodStart(),
                q.getPeriodEnd(),
                q.getLimitKg(),
                excludeId
        );
    }

    // === Доп. методы (не из интерфейса) — оставил на случай, что их дергают где-то ещё ===

    @Transactional(readOnly = true)
    public List<AllocationQuota> findActiveFor(Long organizationId, Long speciesId, Long regionId, LocalDate date) {
        List<AllocationQuota> result = new ArrayList<>();
        repository.findForOrgAndDate(speciesId, regionId, date, organizationId).ifPresent(result::add);
        repository.findGlobalForDate(speciesId, regionId, date).ifPresent(result::add);
        return result;
    }

    @Transactional(readOnly = true)
    public Page<AllocationQuota> findByOrganizationId(Long organizationId, Pageable pageable) {
        return repository.findByOrganizationId(organizationId, pageable);
    }
}
