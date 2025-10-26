package ru.dstu.work.akselerator.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dstu.work.akselerator.entity.CatchReport;
import ru.dstu.work.akselerator.repository.CatchReportRepository;
import ru.dstu.work.akselerator.service.CatchReportService;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class CatchReportServiceImpl implements CatchReportService {

    private final CatchReportRepository repository;

    @Autowired
    public CatchReportServiceImpl(CatchReportRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public CatchReport create(CatchReport report) {
        return repository.save(report);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CatchReport> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CatchReport> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    @Transactional
    public CatchReport update(CatchReport report) {
        return repository.save(report);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CatchReport> findByReportedBy(Long reportedById, Pageable pageable) {
        return repository.findByReportedById(reportedById, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CatchReport> findByOrganization(Long organizationId, Pageable pageable) {
        return repository.findByOrganizationId(organizationId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CatchReport> findByFishingDateBetween(LocalDate start, LocalDate end, Pageable pageable) {
        return repository.findByFishingDateBetween(start, end, pageable);
    }
}
