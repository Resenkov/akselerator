package ru.dstu.work.akselerator.service;

import ru.dstu.work.akselerator.dto.RegionTotalQuotaDto;
import java.util.List;

public interface RegionTotalQuotaService {
    RegionTotalQuotaDto create(RegionTotalQuotaDto dto);
    RegionTotalQuotaDto update(Long id, RegionTotalQuotaDto dto);
    void delete(Long id);
    RegionTotalQuotaDto get(Long id);
    List<RegionTotalQuotaDto> list();
}
