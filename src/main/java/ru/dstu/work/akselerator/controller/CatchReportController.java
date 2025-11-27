package ru.dstu.work.akselerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.dstu.work.akselerator.dto.CatchReportDto;
import ru.dstu.work.akselerator.dto.CreateCatchResult;
import ru.dstu.work.akselerator.dto.OrganizationCatchStatsDto;
import ru.dstu.work.akselerator.entity.CatchReport;
import ru.dstu.work.akselerator.mapper.CatchReportMapper;
import ru.dstu.work.akselerator.service.CatchReportService;

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

    /**
     * Создание отчёта о вылове.
     * Доступно только компании (роль fisherman).
     * Организация подставляется из текущего юзера в сервисе.
     */
    @PreAuthorize("hasRole('FISHERMAN')")
    @PostMapping
    public ResponseEntity<?> create(@Validated @RequestBody CatchReportDto dto) {
        CreateCatchResult result = service.createCatch(dto);
        Map<String, Object> body = new HashMap<>();
        body.put("report", CatchReportMapper.toDto(result.getReport()));
        body.put("warning", result.getWarning()); // предупреждение по квоте “на будущее”
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    /**
     * Отчёты по организации текущего пользователя (роль fisherman).
     */
    @PreAuthorize("hasRole('FISHERMAN')")
    @GetMapping("/my")
    public ResponseEntity<Page<CatchReportDto>> myReports(Pageable pageable) {
        Page<CatchReport> page = service.findMyReports(pageable);
        Page<CatchReportDto> dtoPage = page.map(CatchReportMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }

    /**
     * Список всех неподтверждённых отчётов (is_verified = false).
     * Только для admin.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<Page<CatchReportDto>> pending(Pageable pageable) {
        Page<CatchReport> page = service.findPending(pageable);
        Page<CatchReportDto> dtoPage = page.map(CatchReportMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }


    /**
     * Подтверждение отчёта администратором.
     * Ставит is_verified = true, квоты начинают учитывать этот улов.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/verify")
    public ResponseEntity<?> verify(@PathVariable Long id) {
        CreateCatchResult result = service.verify(id);
        Map<String, Object> body = new HashMap<>();
        body.put("report", CatchReportMapper.toDto(result.getReport()));
        body.put("warning", result.getWarning()); // предупреждение/ошибка по квоте, если есть
        return ResponseEntity.ok(body);
    }

    /**
     * Снятие подтверждения (is_verified = false).
     * Тоже только администратор.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/unverify")
    public ResponseEntity<Void> unverify(@PathVariable Long id) {
        service.unverify(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Список всех отчётов – логично дать только админу.
     * Если хочешь, можно сделать отдельный endpoint "мои отчёты" для fisherman,
     * где внутри сервиса фильтрация по organization текущего пользователя.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<CatchReportDto>> list(Pageable pageable) {
        Page<CatchReport> page = service.list(pageable);
        Page<CatchReportDto> dtoPage = page.map(CatchReportMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }

    /**
     * 3 последних отчёта по организации — тоже, скорее всего, только для admin
     * (или можешь оставить открытым, если это нужно фронту).
     */
    @GetMapping("/organization/{id}/last3")
    public ResponseEntity<Page<CatchReportDto>> findLast3ByOrganization(@PathVariable Long id) {
        Page<CatchReport> page = service.findLast3ByOrganization(id);
        Page<CatchReportDto> dtoPage = page.map(CatchReportMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }

    /**
     * Статистика по организации — тоже админский функционал.
     */
    @PreAuthorize("hasRole('FISHERMAN')")
    @GetMapping("/organization/{id}/stats")
    public ResponseEntity<OrganizationCatchStatsDto> getOrganizationStats(@PathVariable Long id) {
        OrganizationCatchStatsDto stats = service.getOrganizationStats(id);
        return ResponseEntity.ok(stats);
    }

    /**
     * Все отчёты по организации — для админа (или в будущем для компании-хозяина).
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/organization/{id}")
    public ResponseEntity<Page<CatchReportDto>> findByOrganizationId(@PathVariable Long id, Pageable pageable) {
        Page<CatchReport> page = service.findByOrganization(id, pageable);
        Page<CatchReportDto> dtoPage = page.map(CatchReportMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }

    /**
     * Получить один отчёт — сейчас даём админу.
     * Можно сделать отдельную логику, чтобы fisherman видел только свои отчёты.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CatchReportDto> get(@PathVariable Long id) {
        return service.getById(id)
                .map(CatchReportMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Обновление отчёта — обычно это тоже админская история
     * (или вообще запретить обновлять после создания).
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CatchReportDto> update(@PathVariable Long id, @Validated @RequestBody CatchReportDto dto) {
        CatchReport entity = CatchReportMapper.toEntity(dto);
        entity.setId(id);
        CatchReport updated = service.update(entity);
        return ResponseEntity.ok(CatchReportMapper.toDto(updated));
    }

    /**
     * Удаление отчёта — только админ.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
