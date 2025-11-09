package ru.dstu.work.akselerator.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.dstu.work.akselerator.dto.RegionTotalQuotaDto;
import ru.dstu.work.akselerator.service.RegionTotalQuotaService;

import java.util.List;

@RestController
@RequestMapping("/api/region-total-quotas")
@PreAuthorize("hasRole('ADMIN')")
public class RegionTotalQuotaController {

    private final RegionTotalQuotaService service;

    public RegionTotalQuotaController(RegionTotalQuotaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<RegionTotalQuotaDto>> list() {
        return ResponseEntity.ok(service.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegionTotalQuotaDto> get(@PathVariable Long id) {
        var dto = service.get(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody RegionTotalQuotaDto dto) {
        return ResponseEntity.status(201).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody RegionTotalQuotaDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
