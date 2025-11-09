package ru.dstu.work.akselerator.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dstu.work.akselerator.dto.RegionalQuotaDto;
import ru.dstu.work.akselerator.entity.RegionalQuota;
import ru.dstu.work.akselerator.mapper.RegionalQuotaMapper;
import ru.dstu.work.akselerator.repository.RegionalQuotaRepository;
import ru.dstu.work.akselerator.repository.AllocationQuotaRepository;
import ru.dstu.work.akselerator.service.RegionalQuotaService;
import ru.dstu.work.akselerator.exception.QuotaExceededException;
import ru.dstu.work.akselerator.dto.WarningInfo;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RegionalQuotaServiceImpl implements RegionalQuotaService {

    private final RegionalQuotaRepository regionalRepo;
    private final AllocationQuotaRepository allocationRepo;

    public RegionalQuotaServiceImpl(RegionalQuotaRepository regionalRepo,
                                    AllocationQuotaRepository allocationRepo) {
        this.regionalRepo = regionalRepo;
        this.allocationRepo = allocationRepo;
    }

    @Override
    public RegionalQuotaDto create(RegionalQuotaDto dto) {
        BigDecimal sumAlloc = allocationRepo.sumLimitKgBySpeciesRegionOverlappingPeriod(
                dto.getSpeciesId(), dto.getRegionId(), dto.getPeriodStart(), dto.getPeriodEnd(), null);

        if (sumAlloc.compareTo(dto.getLimitKg()) > 0) {
            WarningInfo warning = new WarningInfo();
            warning.setLevel("ERROR");
            warning.setMessage(String.format("Существующие мини-квоты уже суммарно равны %s кг и превышают устанавливаемую региональную квоту %s кг",
                    sumAlloc.toPlainString(), dto.getLimitKg().toPlainString()));
            warning.setQuotaLimitKg(dto.getLimitKg());
            warning.setUsedKg(sumAlloc);
            warning.setRemainingKg(dto.getLimitKg().subtract(sumAlloc));
            throw new  QuotaExceededException(warning);
        }

        RegionalQuota entity = RegionalQuotaMapper.toEntity(dto);
        RegionalQuota saved = regionalRepo.save(entity);
        return RegionalQuotaMapper.toDto(saved);
    }

    @Override
    public RegionalQuotaDto update(Long id, RegionalQuotaDto dto) {
        RegionalQuota existing = regionalRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Regional quota not found"));
        BigDecimal sumAlloc = allocationRepo.sumLimitKgBySpeciesRegionOverlappingPeriod(
                dto.getSpeciesId(), dto.getRegionId(), dto.getPeriodStart(), dto.getPeriodEnd(), null);

        if (sumAlloc.compareTo(dto.getLimitKg()) > 0) {
            WarningInfo warning = new WarningInfo();
            warning.setLevel("ERROR");
            warning.setMessage(String.format("Сумма мини-квот (%s кг) превышает новый региональный лимит (%s кг)",
                    sumAlloc.toPlainString(), dto.getLimitKg().toPlainString()));
            warning.setQuotaLimitKg(dto.getLimitKg());
            warning.setUsedKg(sumAlloc);
            warning.setRemainingKg(dto.getLimitKg().subtract(sumAlloc));
            throw new  QuotaExceededException(warning);
        }

        existing.setPeriodStart(dto.getPeriodStart());
        existing.setPeriodEnd(dto.getPeriodEnd());
        existing.setLimitKg(dto.getLimitKg());

        var s = new ru.dstu.work.akselerator.entity.FishSpecies();
        s.setId(dto.getSpeciesId());
        existing.setSpecies(s);

        var r = new ru.dstu.work.akselerator.entity.FishingRegion();
        r.setId(dto.getRegionId());
        existing.setRegion(r);

        RegionalQuota updated = regionalRepo.save(existing);
        return RegionalQuotaMapper.toDto(updated);
    }

    @Override
    public void delete(Long id) {
        regionalRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public RegionalQuotaDto get(Long id) {
        return regionalRepo.findById(id).map(RegionalQuotaMapper::toDto).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegionalQuotaDto> listAll() {
        return regionalRepo.findAll().stream().map(RegionalQuotaMapper::toDto).collect(Collectors.toList());
    }
}
