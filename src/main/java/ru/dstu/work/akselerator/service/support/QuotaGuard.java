package ru.dstu.work.akselerator.service.support;

import org.springframework.stereotype.Component;
import ru.dstu.work.akselerator.dto.WarningInfo;
import ru.dstu.work.akselerator.entity.RegionalQuota;
import ru.dstu.work.akselerator.exception.QuotaExceededException;
import ru.dstu.work.akselerator.repository.AllocationQuotaRepository;
import ru.dstu.work.akselerator.repository.RegionalQuotaRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class QuotaGuard {

    private final RegionalQuotaRepository regionalRepo;
    private final AllocationQuotaRepository allocationRepo;

    public QuotaGuard(RegionalQuotaRepository regionalRepo, AllocationQuotaRepository allocationRepo) {
        this.regionalRepo = regionalRepo;
        this.allocationRepo = allocationRepo;
    }

    public void checkAllocationAgainstRegional(Long speciesId, Long regionId,
                                               LocalDate periodStart, LocalDate periodEnd,
                                               BigDecimal limitKg, Long excludeAllocationId) {
        List<RegionalQuota> regionals = regionalRepo.findOverlapping(speciesId, regionId, periodStart, periodEnd);
        for (RegionalQuota rq : regionals) {
            BigDecimal used = allocationRepo.sumLimitKgBySpeciesRegionOverlappingPeriod(
                    speciesId, regionId, rq.getPeriodStart(), rq.getPeriodEnd(), excludeAllocationId);
            BigDecimal after = used.add(limitKg);
            if (after.compareTo(rq.getLimitKg()) > 0) {
                WarningInfo w = new WarningInfo();
                w.setLevel("ERROR");
                w.setMessage(String.format("Превышение региональной квоты: лимит %s кг, суммарно после операции будет %s кг.",
                        rq.getLimitKg().toPlainString(), after.toPlainString()));
                w.setQuotaLimitKg(rq.getLimitKg());
                w.setUsedKg(used);
                w.setRemainingKg(rq.getLimitKg().subtract(used));
                throw new QuotaExceededException(w);
            }
        }
    }
}
