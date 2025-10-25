package ru.dstu.work.akselerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.dstu.work.akselerator.dto.FishingRegionDto;
import ru.dstu.work.akselerator.mapper.FishingRegionMapper;
import ru.dstu.work.akselerator.service.FishingRegionService;
import ru.dstu.work.akselerator.entity.FishingRegion;

@RestController
@RequestMapping("/api/fishing-regions")
public class FishingRegionController {

    private final FishingRegionService service;

    @Autowired
    public FishingRegionController(FishingRegionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<FishingRegionDto>> list(Pageable pageable) {
        Page<FishingRegion> page = service.list(pageable);
        Page<FishingRegionDto> dtoPage = page.map(FishingRegionMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/" + "{" + "id" + "}")
    public ResponseEntity<FishingRegionDto> get(@PathVariable Long id) {
        return service.getById(id)
                .map(FishingRegionMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FishingRegionDto> create(@Validated @RequestBody FishingRegionDto dto) {
        FishingRegion entity = FishingRegionMapper.toEntity(dto);
        FishingRegion saved = service.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(FishingRegionMapper.toDto(saved));
    }

    @PutMapping("/" + "{" + "id" + "}")
    public ResponseEntity<FishingRegionDto> update(@PathVariable Long id, @Validated @RequestBody FishingRegionDto dto) {
        FishingRegion entity = FishingRegionMapper.toEntity(dto);
        try {
            entity.getClass().getMethod("setId", Long.class).invoke(entity, id);
        } catch (Exception e) {
            // ignore - service may handle id mapping
        }
        FishingRegion updated = service.update(entity);
        return ResponseEntity.ok(FishingRegionMapper.toDto(updated));
    }

    @DeleteMapping("/" + "{" + "id" + "}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}