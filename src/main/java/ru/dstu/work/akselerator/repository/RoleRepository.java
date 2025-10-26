package ru.dstu.work.akselerator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.dstu.work.akselerator.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
