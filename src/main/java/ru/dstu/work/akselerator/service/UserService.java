package ru.dstu.work.akselerator.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.dstu.work.akselerator.entity.User;

import java.util.Optional;

public interface UserService {
    User create(User user);
    Optional<User> getById(Long id);
    Page<User> list(Pageable pageable);
    User update(User user);
    void deleteById(Long id);
    Optional<User> findByUsername(String username);
}
