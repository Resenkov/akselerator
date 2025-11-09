package ru.dstu.work.akselerator.service;

import ru.dstu.work.akselerator.dto.RegionalQuotaDto;
import java.util.List;

public interface RegionalQuotaService {
    RegionalQuotaDto create(RegionalQuotaDto dto);
    RegionalQuotaDto update(Long id, RegionalQuotaDto dto);
    void delete(Long id);
    RegionalQuotaDto get(Long id);
    List<RegionalQuotaDto> listAll();
}
