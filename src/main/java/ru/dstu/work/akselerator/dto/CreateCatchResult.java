package ru.dstu.work.akselerator.dto;

import ru.dstu.work.akselerator.entity.CatchReport;

/**
 * Результат создания отчёта об улове: сохранённая сущность + опциональное предупреждение.
 */
public class CreateCatchResult {

    private CatchReport report;
    private WarningInfo warning;

    public CreateCatchResult() { }

    public CreateCatchResult(CatchReport report, WarningInfo warning) {
        this.report = report;
        this.warning = warning;
    }

    public CatchReport getReport() {
        return report;
    }

    public void setReport(CatchReport report) {
        this.report = report;
    }

    public WarningInfo getWarning() {
        return warning;
    }

    public void setWarning(WarningInfo warning) {
        this.warning = warning;
    }
}
