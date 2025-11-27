package ru.dstu.work.akselerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.dstu.work.akselerator.dto.CatchReportDto;
import ru.dstu.work.akselerator.dto.CreateCatchResult;
import ru.dstu.work.akselerator.dto.OrganizationCatchStatsDto;
import ru.dstu.work.akselerator.mapper.CatchReportMapper;
import ru.dstu.work.akselerator.service.CatchReportService;
import ru.dstu.work.akselerator.entity.CatchReport;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/catch-reports")
public class CatchReportController {

    private final CatchReportService service;

    @Autowired
    public CatchReportController(CatchReportService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> create(@Validated @RequestBody CatchReportDto dto) {
        CreateCatchResult result = service.createCatch(dto);
        Map<String,Object> body = new HashMap<>();
        body.put("report", CatchReportMapper.toDto(result.getReport()));
        body.put("warning", result.getWarning());
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping
    public ResponseEntity<Page<CatchReportDto>> list(Pageable pageable) {
        Page<CatchReport> page = service.list(pageable);
        Page<CatchReportDto> dtoPage = page.map(CatchReportMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/organization/{id}/last3")
    public ResponseEntity<Page<CatchReportDto>> findLast3ByOrganization(@PathVariable Long id) {
        Page<CatchReport> page = service.findLast3ByOrganization(id);
        Page<CatchReportDto> dtoPage = page.map(CatchReportMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/organization/{id}/stats")
    public ResponseEntity<OrganizationCatchStatsDto> getOrganizationStats(@PathVariable Long id) {
        OrganizationCatchStatsDto stats = service.getOrganizationStats(id);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/organization/{id}")
    public ResponseEntity<Page<CatchReportDto>> findByOrganizationId(@PathVariable Long id, Pageable pageable){
        Page<CatchReport> page = service.findByOrganization(id, pageable);
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


    @PutMapping("/{id}")
    public ResponseEntity<CatchReportDto> update(@PathVariable Long id, @Validated @RequestBody CatchReportDto dto) {
        CatchReport entity = CatchReportMapper.toEntity(dto);
        entity.setId(id);
        CatchReport updated = service.update(entity);
        return ResponseEntity.ok(CatchReportMapper.toDto(updated));
    }

    @DeleteMapping("/" + "{" + "id" + "}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}