package ru.dstu.work.akselerator.exception;

import ru.dstu.work.akselerator.dto.WarningInfo;

public class QuotaExceededException extends RuntimeException {
    private final WarningInfo warning;

    public QuotaExceededException(WarningInfo warning) {
        super(warning == null ? "quota exceeded" : warning.getMessage());
        this.warning = warning;
    }

    public WarningInfo getWarning() {
        return warning;
    }
}
