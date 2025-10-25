package ru.dstu.work.akselerator.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dstu.work.akselerator.entity.User;
import ru.dstu.work.akselerator.repository.UserRepository;
import ru.dstu.work.akselerator.service.UserService;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public User create(User user) {
        return repository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    @Transactional
    public User update(User user) {
        return repository.save(user);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }
}
