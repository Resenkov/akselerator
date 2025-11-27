package ru.dstu.work.akselerator.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class OrganizationCatchStatsDto {

    private Long organizationId;
    private Long totalCatches;
    private BigDecimal totalWeightKg;
    private Long catchesThisMonth;
    private Long mostFrequentRegionId;
    private String mostFrequentRegionName;

    public OrganizationCatchStatsDto() {
    }

    public OrganizationCatchStatsDto(Long organizationId,
                                     Long totalCatches,
                                     BigDecimal totalWeightKg,
                                     Long catchesThisMonth,
                                     Long mostFrequentRegionId,
                                     String mostFrequentRegionName) {
        this.organizationId = organizationId;
        this.totalCatches = totalCatches;
        this.totalWeightKg = totalWeightKg;
        this.catchesThisMonth = catchesThisMonth;
        this.mostFrequentRegionId = mostFrequentRegionId;
        this.mostFrequentRegionName = mostFrequentRegionName;
    }

}
