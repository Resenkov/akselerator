package ru.dstu.work.akselerator.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dstu.work.akselerator.entity.FishingRegion;
import ru.dstu.work.akselerator.repository.FishingRegionRepository;
import ru.dstu.work.akselerator.service.FishingRegionService;

import java.util.Optional;
import java.util.List;

@Service
public class FishingRegionServiceImpl implements FishingRegionService {
    private final FishingRegionRepository repository;

    @Autowired
    public FishingRegionServiceImpl(FishingRegionRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public FishingRegion create(FishingRegion entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FishingRegion> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FishingRegion> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FishingRegion> listAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public FishingRegion update(FishingRegion entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
