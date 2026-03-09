package ru.dstu.work.akselerator.parkingaccount;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {
    private final String userId;
    private BigDecimal available = BigDecimal.ZERO;
    private BigDecimal frozen = BigDecimal.ZERO;

    public Account(String userId) {
        this.userId = Objects.requireNonNull(userId);
    }

    public String getUserId() {
        return userId;
    }

    public BigDecimal getAvailable() {
        return available;
    }

    public BigDecimal getFrozen() {
        return frozen;
    }

    void increaseAvailable(BigDecimal value) {
        this.available = this.available.add(value);
    }

    void decreaseAvailable(BigDecimal value) {
        this.available = this.available.subtract(value);
    }

    void increaseFrozen(BigDecimal value) {
        this.frozen = this.frozen.add(value);
    }

    void decreaseFrozen(BigDecimal value) {
        this.frozen = this.frozen.subtract(value);
    }
}
