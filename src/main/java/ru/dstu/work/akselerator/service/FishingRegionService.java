package ru.dstu.work.akselerator.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.dstu.work.akselerator.entity.FishingRegion;

import java.util.Optional;
import java.util.List;


public interface FishingRegionService {
    FishingRegion create(FishingRegion entity);
    Optional<FishingRegion> getById(Long id);
    Page<FishingRegion> list(Pageable pageable);
    List<FishingRegion> listAll();
    FishingRegion update(FishingRegion entity);
    void deleteById(Long id);
}
