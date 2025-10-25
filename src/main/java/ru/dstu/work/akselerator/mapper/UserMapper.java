package ru.dstu.work.akselerator.mapper;

import ru.dstu.work.akselerator.dto.UserDto;
import ru.dstu.work.akselerator.entity.User;
import ru.dstu.work.akselerator.entity.Organization;

public class UserMapper {
    public static UserDto toDto(User e) {
        if (e == null) return null;
        UserDto d = new UserDto();
        d.setId(e.getId());
        d.setOrganizationId(e.getOrganization() == null ? null : e.getOrganization().getId());
        d.setUsername(e.getUsername());
        d.setEmail(e.getEmail());
        d.setActive(e.isActive());
        return d;
    }
    public static User toEntity(UserDto d) {
        if (d == null) return null;
        User e = new User();
        e.setUsername(d.getUsername());
        e.setEmail(d.getEmail());
        e.setPasswordHash(d.getPassword()); // hashing should be done in service layer
        e.setActive(d.isActive());
        if (d.getOrganizationId() != null) {
            Organization org = new Organization();
            org.setId(d.getOrganizationId());
            e.setOrganization(org);
        }
        return e;
    }
}
