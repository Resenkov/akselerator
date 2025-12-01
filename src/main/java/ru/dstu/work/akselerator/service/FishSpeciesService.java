package ru.dstu.work.akselerator.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.dstu.work.akselerator.entity.FishSpecies;

import java.util.Optional;
import java.util.List;

public interface FishSpeciesService {
    FishSpecies create(FishSpecies entity);
    Optional<FishSpecies> getById(Long id);
    Page<FishSpecies> list(Pageable pageable);
    List<FishSpecies> listAll();
    FishSpecies update(FishSpecies entity);
    void deleteById(Long id);
}
