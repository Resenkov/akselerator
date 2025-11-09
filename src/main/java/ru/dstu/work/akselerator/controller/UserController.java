package ru.dstu.work.akselerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.dstu.work.akselerator.dto.UserDto;
import ru.dstu.work.akselerator.mapper.UserMapper;
import ru.dstu.work.akselerator.service.UserService;
import ru.dstu.work.akselerator.entity.User;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<UserDto>> list(Pageable pageable) {
        Page<User> page = service.list(pageable);
        Page<UserDto> dtoPage = page.map(UserMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/" + "{" + "id" + "}")
    public ResponseEntity<UserDto> get(@PathVariable Long id) {
        return service.getById(id)
                .map(UserMapper::toDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@Validated @RequestBody UserDto dto) {
        User entity = UserMapper.toEntity(dto);
        User saved = service.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @Validated @RequestBody UserDto dto) {
        User entity = UserMapper.toEntity(dto);
        entity.setId(id);
        User updated = service.update(entity);
        return ResponseEntity.ok(UserMapper.toDto(updated));
    }

    @DeleteMapping("/" + "{" + "id" + "}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}