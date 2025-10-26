package ru.dstu.work.akselerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.dstu.work.akselerator.dto.RoleDto;
import ru.dstu.work.akselerator.mapper.RoleMapper;
import ru.dstu.work.akselerator.service.RoleService;
import ru.dstu.work.akselerator.entity.Role;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService service;

    @Autowired
    public RoleController(RoleService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<RoleDto>> list(Pageable pageable) {
        Page<Role> page = service.list(pageable);
        Page<RoleDto> dtoPage = page.map(RoleMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/" + "{" + "id" + "}")
    public ResponseEntity<RoleDto> get(@PathVariable Long id) {
        return service.getById(id)
                .map(RoleMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RoleDto> create(@Validated @RequestBody RoleDto dto) {
        Role entity = RoleMapper.toEntity(dto);
        Role saved = service.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(RoleMapper.toDto(saved));
    }

    @PutMapping("/" + "{" + "id" + "}")
    public ResponseEntity<RoleDto> update(@PathVariable Long id, @Validated @RequestBody RoleDto dto) {
        Role entity = RoleMapper.toEntity(dto);
        try {
            entity.getClass().getMethod("setId", Long.class).invoke(entity, id);
        } catch (Exception e) {
            // ignore - service may handle id mapping
        }
        Role updated = service.update(entity);
        return ResponseEntity.ok(RoleMapper.toDto(updated));
    }

    @DeleteMapping("/" + "{" + "id" + "}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}