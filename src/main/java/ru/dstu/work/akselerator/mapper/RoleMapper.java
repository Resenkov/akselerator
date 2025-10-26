package ru.dstu.work.akselerator.mapper;

import ru.dstu.work.akselerator.dto.RoleDto;
import ru.dstu.work.akselerator.entity.Role;

public class RoleMapper {
    public static RoleDto toDto(Role e) {
        if (e == null) return null;
        return new RoleDto(e.getId(), e.getName(), e.getDescription());
    }
    public static Role toEntity(RoleDto d) {
        if (d == null) return null;
        Role e = new Role();
        e.setName(d.getName());
        e.setDescription(d.getDescription());
        return e;
    }
}
