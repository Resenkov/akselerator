package ru.dstu.work.akselerator.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;

/**
 * Информация о предупреждении по квоте, возвращаемая при создании отчёта об улове.
 */
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BigDecimal getQuotaLimitKg() {
        return quotaLimitKg;
    }

    public void setQuotaLimitKg(BigDecimal quotaLimitKg) {
        this.quotaLimitKg = quotaLimitKg;
    }

    public BigDecimal getUsedKg() {
        return usedKg;
    }

    public void setUsedKg(BigDecimal usedKg) {
        this.usedKg = usedKg;
    }

    public BigDecimal getRemainingKg() {
        return remainingKg;
    }

    public void setRemainingKg(BigDecimal remainingKg) {
        this.remainingKg = remainingKg;
    }

    public BigDecimal getPercentUsed() {
        return percentUsed;
    }

    public void setPercentUsed(BigDecimal percentUsed) {
        this.percentUsed = percentUsed;
    }
}
