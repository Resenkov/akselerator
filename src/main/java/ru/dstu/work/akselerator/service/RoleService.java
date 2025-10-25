package ru.dstu.work.akselerator.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.dstu.work.akselerator.entity.Role;

import java.util.Optional;

public interface RoleService {
    Role create(Role entity);
    Optional<Role> getById(Long id);
    Page<Role> list(Pageable pageable);
    Role update(Role entity);
    void deleteById(Long id);
}
