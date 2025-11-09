package ru.dstu.work.akselerator.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dstu.work.akselerator.dto.RegionTotalQuotaDto;
import ru.dstu.work.akselerator.dto.WarningInfo;
import ru.dstu.work.akselerator.entity.RegionTotalQuota;
import ru.dstu.work.akselerator.exception.QuotaExceededException;
import ru.dstu.work.akselerator.mapper.RegionTotalQuotaMapper;
import ru.dstu.work.akselerator.repository.AllocationQuotaRepository;
import ru.dstu.work.akselerator.repository.RegionTotalQuotaRepository;
import ru.dstu.work.akselerator.service.RegionTotalQuotaService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RegionTotalQuotaServiceImpl implements RegionTotalQuotaService {

    private final RegionTotalQuotaRepository regionRepo;
    private final AllocationQuotaRepository allocationRepo;

    public RegionTotalQuotaServiceImpl(RegionTotalQuotaRepository regionRepo,
                                       AllocationQuotaRepository allocationRepo) {
        this.regionRepo = regionRepo;
        this.allocationRepo = allocationRepo;
    }

    @Override
    public RegionTotalQuotaDto create(RegionTotalQuotaDto dto) {
        // Проверяем, что существующие мини-квоты не превышают новый общий лимит:
        BigDecimal used = allocationRepo.sumLimitKgByRegionOverlappingPeriod(
                dto.getRegionId(), dto.getPeriodStart(), dto.getPeriodEnd(), null);

        if (used.compareTo(dto.getLimitKg()) > 0) {
            throw quotaWarn("Сумма мини-квот уже превышает устанавливаемый общий лимит.",
                    dto.getLimitKg(), used);
        }
        RegionTotalQuota saved = regionRepo.save(RegionTotalQuotaMapper.toEntity(dto));
        return RegionTotalQuotaMapper.toDto(saved);
    }

    @Override
    public RegionTotalQuotaDto update(Long id, RegionTotalQuotaDto dto) {
        RegionTotalQuota existing = regionRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RegionTotalQuota not found: id=" + id));

        BigDecimal used = allocationRepo.sumLimitKgByRegionOverlappingPeriod(
                dto.getRegionId(), dto.getPeriodStart(), dto.getPeriodEnd(), null);

        if (used.compareTo(dto.getLimitKg()) > 0) {
            throw quotaWarn("Сумма мини-квот превышает новый общий лимит.", dto.getLimitKg(), used);
        }

        existing.setLimitKg(dto.getLimitKg());
        var fr = new ru.dstu.work.akselerator.entity.FishingRegion();
        fr.setId(dto.getRegionId());
        existing.setRegion(fr);
        existing.setPeriodStart(dto.getPeriodStart());
        existing.setPeriodEnd(dto.getPeriodEnd());

        return RegionTotalQuotaMapper.toDto(regionRepo.save(existing));
    }

    @Override
    public void delete(Long id) {
        regionRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public RegionTotalQuotaDto get(Long id) {
        return regionRepo.findById(id).map(RegionTotalQuotaMapper::toDto).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RegionTotalQuotaDto> list() {
        return regionRepo.findAll().stream().map(RegionTotalQuotaMapper::toDto).collect(Collectors.toList());
    }

    private QuotaExceededException quotaWarn(String msg, BigDecimal limit, BigDecimal used) {
        var w = new WarningInfo();
        w.setLevel("ERROR");
        w.setMessage(msg);
        w.setQuotaLimitKg(limit);
        w.setUsedKg(used);
        w.setRemainingKg(limit.subtract(used));
        if (limit.signum() > 0) {
            w.setPercentUsed(used.divide(limit, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")));
        }
        return new QuotaExceededException(w);
    }
}
