package ru.dstu.work.akselerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.dstu.work.akselerator.dto.CatchReportDto;
import ru.dstu.work.akselerator.mapper.CatchReportMapper;
import ru.dstu.work.akselerator.service.CatchReportService;
import ru.dstu.work.akselerator.entity.CatchReport;

@RestController
@RequestMapping("/api/catch-reports")
public class CatchReportController {

    private final CatchReportService service;

    @Autowired
    public CatchReportController(CatchReportService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<CatchReportDto>> list(Pageable pageable) {
        Page<CatchReport> page = service.list(pageable);
        Page<CatchReportDto> dtoPage = page.map(CatchReportMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/" + "{" + "id" + "}")
    public ResponseEntity<CatchReportDto> get(@PathVariable Long id) {
        return service.getById(id)
                .map(CatchReportMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CatchReportDto> create(@Validated @RequestBody CatchReportDto dto) {
        CatchReport entity = CatchReportMapper.toEntity(dto);
        CatchReport saved = service.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(CatchReportMapper.toDto(saved));
    }

    @PutMapping("/" + "{" + "id" + "}")
    public ResponseEntity<CatchReportDto> update(@PathVariable Long id, @Validated @RequestBody CatchReportDto dto) {
        CatchReport entity = CatchReportMapper.toEntity(dto);
        try {
            entity.getClass().getMethod("setId", Long.class).invoke(entity, id);
        } catch (Exception e) {
            // ignore - service may handle id mapping
        }
        CatchReport updated = service.update(entity);
        return ResponseEntity.ok(CatchReportMapper.toDto(updated));
    }

    @DeleteMapping("/" + "{" + "id" + "}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}