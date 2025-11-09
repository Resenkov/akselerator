package ru.dstu.work.akselerator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Информация о предупреждении по квоте, возвращаемая при создании отчёта об улове.
 */
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WarningInfo {

    /**
     * Уровень предупреждения: INFO | WARN | ERROR
     */
    private String level;

    /**
     * Читаемое сообщение для пользователя (например: "Внимание: по треске... осталось 800 кг").
     */
    private String message;

    private BigDecimal quotaLimitKg;
    private BigDecimal usedKg;
    private BigDecimal remainingKg;

    /**
     * Процент использования квоты (например 92.5)
     */
    private BigDecimal percentUsed;

    public WarningInfo() { }

    public WarningInfo(String level, String message,
                       BigDecimal quotaLimitKg,
                       BigDecimal usedKg,
                       BigDecimal remainingKg,
                       BigDecimal percentUsed) {
        this.level = level;
        this.message = message;
        this.quotaLimitKg = quotaLimitKg;
        this.usedKg = usedKg;
        this.remainingKg = remainingKg;
        this.percentUsed = percentUsed;
    }

    // getters / setters

}
