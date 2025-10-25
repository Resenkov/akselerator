package ru.dstu.work.akselerator.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dstu.work.akselerator.entity.FishSpecies;
import ru.dstu.work.akselerator.repository.FishSpeciesRepository;
import ru.dstu.work.akselerator.service.FishSpeciesService;

import java.util.Optional;

@Service
public class FishSpeciesServiceImpl implements FishSpeciesService {
    private final FishSpeciesRepository repository;

    @Autowired
    public FishSpeciesServiceImpl(FishSpeciesRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public FishSpecies create(FishSpecies entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FishSpecies> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FishSpecies> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    @Transactional
    public FishSpecies update(FishSpecies entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
