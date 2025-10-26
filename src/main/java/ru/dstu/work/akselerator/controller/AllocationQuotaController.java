package ru.dstu.work.akselerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.dstu.work.akselerator.dto.AllocationQuotaDto;
import ru.dstu.work.akselerator.mapper.AllocationQuotaMapper;
import ru.dstu.work.akselerator.service.AllocationQuotaService;
import ru.dstu.work.akselerator.entity.AllocationQuota;

@RestController
@RequestMapping("/api/allocation-quotas")
public class AllocationQuotaController {

    private final AllocationQuotaService service;

    @Autowired
    public AllocationQuotaController(AllocationQuotaService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<AllocationQuotaDto>> list(Pageable pageable) {
        Page<AllocationQuota> page = service.list(pageable);
        Page<AllocationQuotaDto> dtoPage = page.map(AllocationQuotaMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/" + "{" + "id" + "}")
    public ResponseEntity<AllocationQuotaDto> get(@PathVariable Long id) {
        return service.getById(id)
                .map(AllocationQuotaMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AllocationQuotaDto> create(@Validated @RequestBody AllocationQuotaDto dto) {
        AllocationQuota entity = AllocationQuotaMapper.toEntity(dto);
        AllocationQuota saved = service.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(AllocationQuotaMapper.toDto(saved));
    }

    @PutMapping("/" + "{" + "id" + "}")
    public ResponseEntity<AllocationQuotaDto> update(@PathVariable Long id, @Validated @RequestBody AllocationQuotaDto dto) {
        AllocationQuota entity = AllocationQuotaMapper.toEntity(dto);
        try {
            entity.getClass().getMethod("setId", Long.class).invoke(entity, id);
        } catch (Exception e) {
            // ignore - service may handle id mapping
        }
        AllocationQuota updated = service.update(entity);
        return ResponseEntity.ok(AllocationQuotaMapper.toDto(updated));
    }

    @DeleteMapping("/" + "{" + "id" + "}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}