package ru.dstu.work.akselerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.dstu.work.akselerator.dto.FishSpeciesDto;
import ru.dstu.work.akselerator.mapper.FishSpeciesMapper;
import ru.dstu.work.akselerator.service.FishSpeciesService;
import ru.dstu.work.akselerator.entity.FishSpecies;

@RestController
@RequestMapping("/api/species")
public class FishSpeciesController {

    private final FishSpeciesService service;

    @Autowired
    public FishSpeciesController(FishSpeciesService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<FishSpeciesDto>> list(Pageable pageable) {
        Page<FishSpecies> page = service.list(pageable);
        Page<FishSpeciesDto> dtoPage = page.map(FishSpeciesMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/" + "{" + "id" + "}")
    public ResponseEntity<FishSpeciesDto> get(@PathVariable Long id) {
        return service.getById(id)
                .map(FishSpeciesMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FishSpeciesDto> create(@Validated @RequestBody FishSpeciesDto dto) {
        FishSpecies entity = FishSpeciesMapper.toEntity(dto);
        FishSpecies saved = service.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(FishSpeciesMapper.toDto(saved));
    }

    @PutMapping("/" + "{" + "id" + "}")
    public ResponseEntity<FishSpeciesDto> update(@PathVariable Long id, @Validated @RequestBody FishSpeciesDto dto) {
        FishSpecies entity = FishSpeciesMapper.toEntity(dto);
        try {
            entity.getClass().getMethod("setId", Long.class).invoke(entity, id);
        } catch (Exception e) {
        }
        FishSpecies updated = service.update(entity);
        return ResponseEntity.ok(FishSpeciesMapper.toDto(updated));
    }

    @DeleteMapping("/" + "{" + "id" + "}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}