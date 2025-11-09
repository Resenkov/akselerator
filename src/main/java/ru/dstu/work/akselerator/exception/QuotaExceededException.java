package ru.dstu.work.akselerator.exception;

import lombok.Getter;
import ru.dstu.work.akselerator.dto.WarningInfo;

@Getter
public class QuotaExceededException extends RuntimeException {
    private final transient WarningInfo warning;

    public QuotaExceededException(WarningInfo warning) {
        super(warning != null ? warning.getMessage() : "Quota exceeded");
        this.warning = warning;
    }

}
