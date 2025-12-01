package ru.dstu.work.akselerator.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.dstu.work.akselerator.entity.Organization;

import java.util.Optional;
import java.util.List;

public interface OrganizationService {
    Organization create(Organization entity);
    Optional<Organization> getById(Long id);
    Page<Organization> list(Pageable pageable);
    List<Organization> listAll();
    Organization update(Organization entity);
    void deleteById(Long id);
}
