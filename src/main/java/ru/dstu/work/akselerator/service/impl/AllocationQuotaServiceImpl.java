package ru.dstu.work.akselerator.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dstu.work.akselerator.entity.AllocationQuota;
import ru.dstu.work.akselerator.repository.AllocationQuotaRepository;
import ru.dstu.work.akselerator.service.AllocationQuotaService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AllocationQuotaServiceImpl implements AllocationQuotaService {

    private final AllocationQuotaRepository repository;

    @Autowired
    public AllocationQuotaServiceImpl(AllocationQuotaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public AllocationQuota create(AllocationQuota quota) {
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
    @Transactional
    public AllocationQuota update(AllocationQuota quota) {
        return repository.save(quota);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AllocationQuota> findByOrganizationId(Long organizationId, Pageable pageable) {
        return repository.findByOrganizationId(organizationId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AllocationQuota> findActiveFor(Long organizationId, Long speciesId, Long regionId, LocalDate date) {
        return repository.findByOrganizationIdAndSpeciesIdAndRegionIdAndPeriodStartLessThanEqualAndPeriodEndGreaterThanEqual(
                organizationId, speciesId, regionId, date, date
        );
    }
}
