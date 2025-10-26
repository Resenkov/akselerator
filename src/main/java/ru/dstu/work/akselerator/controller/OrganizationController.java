package ru.dstu.work.akselerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.dstu.work.akselerator.dto.OrganizationDto;
import ru.dstu.work.akselerator.mapper.OrganizationMapper;
import ru.dstu.work.akselerator.service.OrganizationService;
import ru.dstu.work.akselerator.entity.Organization;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService service;

    @Autowired
    public OrganizationController(OrganizationService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<OrganizationDto>> list(Pageable pageable) {
        Page<Organization> page = service.list(pageable);
        Page<OrganizationDto> dtoPage = page.map(OrganizationMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/" + "{" + "id" + "}")
    public ResponseEntity<OrganizationDto> get(@PathVariable Long id) {
        return service.getById(id)
                .map(OrganizationMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<OrganizationDto> create(@Validated @RequestBody OrganizationDto dto) {
        Organization entity = OrganizationMapper.toEntity(dto);
        Organization saved = service.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrganizationMapper.toDto(saved));
    }

    @PutMapping("/" + "{" + "id" + "}")
    public ResponseEntity<OrganizationDto> update(@PathVariable Long id, @Validated @RequestBody OrganizationDto dto) {
        Organization entity = OrganizationMapper.toEntity(dto);
        try {
            entity.getClass().getMethod("setId", Long.class).invoke(entity, id);
        } catch (Exception e) {
            // ignore - service may handle id mapping
        }
        Organization updated = service.update(entity);
        return ResponseEntity.ok(OrganizationMapper.toDto(updated));
    }

    @DeleteMapping("/" + "{" + "id" + "}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}