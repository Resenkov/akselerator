package ru.dstu.work.akselerator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dstu.work.akselerator.dto.*;
import ru.dstu.work.akselerator.mapper.FishSpeciesMapper;
import ru.dstu.work.akselerator.mapper.FishingRegionMapper;
import ru.dstu.work.akselerator.mapper.OrganizationMapper;
import ru.dstu.work.akselerator.service.FishSpeciesService;
import ru.dstu.work.akselerator.service.FishingRegionService;
import ru.dstu.work.akselerator.service.OrganizationService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер, возвращающий справочные данные для экранов установки квот.
 */
@RestController
@RequestMapping("/api/quotas/reference")
public class QuotaReferenceController {

    private final FishSpeciesService fishSpeciesService;
    private final FishingRegionService fishingRegionService;
    private final OrganizationService organizationService;

    public QuotaReferenceController(FishSpeciesService fishSpeciesService,
                                    FishingRegionService fishingRegionService,
                                    OrganizationService organizationService) {
        this.fishSpeciesService = fishSpeciesService;
        this.fishingRegionService = fishingRegionService;
        this.organizationService = organizationService;
    }

    /**
     * Справочник для общих региональных квот: все виды рыбы и регионы.
     */
    @GetMapping("/region-total")
    public ResponseEntity<RegionQuotaReferenceDto> regionTotalReferences() {
        List<FishSpeciesDto> species = fishSpeciesService.listAll().stream()
                .map(FishSpeciesMapper::toDto)
                .collect(Collectors.toList());

        List<FishingRegionDto> regions = fishingRegionService.listAll().stream()
                .map(FishingRegionMapper::toDto)
                .collect(Collectors.toList());

        RegionQuotaReferenceDto dto = new RegionQuotaReferenceDto(species, regions);
        return ResponseEntity.ok(dto);
    }

    /**
     * Справочник для квот компаний: все виды рыбы, регионы и организации.
     */
    @GetMapping("/allocation")
    public ResponseEntity<AllocationQuotaReferenceDto> allocationReferences() {
        List<FishSpeciesDto> species = fishSpeciesService.listAll().stream()
                .map(FishSpeciesMapper::toDto)
                .collect(Collectors.toList());

        List<FishingRegionDto> regions = fishingRegionService.listAll().stream()
                .map(FishingRegionMapper::toDto)
                .collect(Collectors.toList());

        List<OrganizationDto> organizations = organizationService.listAll().stream()
                .map(OrganizationMapper::toDto)
                .collect(Collectors.toList());

        AllocationQuotaReferenceDto dto = new AllocationQuotaReferenceDto(species, regions, organizations);
        return ResponseEntity.ok(dto);
    }
}
