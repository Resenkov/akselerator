package ru.dstu.work.akselerator.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.dstu.work.akselerator.entity.FishingRegion;

import java.util.Optional;


public interface FishingRegionService {
    FishingRegion create(FishingRegion entity);
    Optional<FishingRegion> getById(Long id);
    Page<FishingRegion> list(Pageable pageable);
    FishingRegion update(FishingRegion entity);
    void deleteById(Long id);
}
