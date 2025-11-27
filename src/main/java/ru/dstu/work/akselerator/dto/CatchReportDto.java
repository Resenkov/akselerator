package ru.dstu.work.akselerator.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
public class CatchReportDto {
    private Long id;

    @NotNull private Long organizationId;
    @NotNull private Long reportedBy;
    @NotNull private Long speciesId;
    @NotNull private Long regionId;
    @NotNull private LocalDate fishingDate;
    @NotNull private BigDecimal weightKg;
    private String notes;
    private boolean verified = false;

    public CatchReportDto() {}

}
