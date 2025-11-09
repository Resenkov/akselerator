package ru.dstu.work.akselerator.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.dstu.work.akselerator.entity.AllocationQuota;


import java.util.Optional;

public interface AllocationQuotaService {
    AllocationQuota create(AllocationQuota quota);
    Optional<AllocationQuota> getById(Long id);
    Page<AllocationQuota> list(Pageable pageable);
    AllocationQuota update(AllocationQuota quota);
    void deleteById(Long id);
}
