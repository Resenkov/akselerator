package ru.dstu.work.akselerator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.dstu.work.akselerator.entity.UserRole;
import ru.dstu.work.akselerator.entity.UserRoleId;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

    @Query("select ur.role.name from UserRole ur where ur.user.id = :userId")
    List<String> findRoleNamesByUserId(@Param("userId") Long userId);
}
