package ru.dstu.work.akselerator.dto;

import lombok.Getter;
import lombok.Setter;
import ru.dstu.work.akselerator.entity.CatchReport;

/**
 * Результат создания отчёта об улове: сохранённая сущность + опциональное предупреждение.
 */
@Setter
@Getter
public class CreateCatchResult {

    private CatchReport report;
    private WarningInfo warning;

    public CreateCatchResult() { }

    public CreateCatchResult(CatchReport report, WarningInfo warning) {
        this.report = report;
        this.warning = warning;
    }

}
