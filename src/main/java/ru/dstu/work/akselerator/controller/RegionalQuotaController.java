package ru.dstu.work.akselerator.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.dstu.work.akselerator.dto.RegionalQuotaDto;
import ru.dstu.work.akselerator.service.RegionalQuotaService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/regional-quotas")
public class RegionalQuotaController {

    private final RegionalQuotaService regionalQuotaService;

    public RegionalQuotaController(RegionalQuotaService regionalQuotaService) {
        this.regionalQuotaService = regionalQuotaService;
    }

    @GetMapping
    public ResponseEntity<List<RegionalQuotaDto>> list() {
        return ResponseEntity.ok(regionalQuotaService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegionalQuotaDto> get(@PathVariable Long id) {
        var dto = regionalQuotaService.get(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody RegionalQuotaDto dto) {
        var created = regionalQuotaService.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody RegionalQuotaDto dto) {
        var updated = regionalQuotaService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        regionalQuotaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
