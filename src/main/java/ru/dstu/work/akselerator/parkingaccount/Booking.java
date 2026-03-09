package ru.dstu.work.akselerator.parkingaccount;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Booking {
    public static final Duration HOLD_TTL = Duration.ofMinutes(5);
    public static final Duration ARRIVAL_WINDOW = Duration.ofMinutes(15);
    public static final Duration SLOT_STEP = Duration.ofMinutes(15);
    public static final Duration MAX_DURATION = Duration.ofHours(12);
    public static final Duration MAX_AHEAD = Duration.ofDays(7);

    private final UUID id;
    private final String userId;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final BigDecimal totalAmount;
    private final LocalDateTime holdExpiresAt;
    private BookingStatus status;

    public Booking(UUID id, String userId, LocalDateTime startAt, LocalDateTime endAt, BigDecimal totalAmount, LocalDateTime now) {
        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId);
        this.startAt = Objects.requireNonNull(startAt);
        this.endAt = Objects.requireNonNull(endAt);
        this.totalAmount = Objects.requireNonNull(totalAmount);
        this.holdExpiresAt = now.plus(HOLD_TTL);
        this.status = BookingStatus.HOLD;
    }

    public UUID getId() { return id; }
    public String getUserId() { return userId; }
    public LocalDateTime getStartAt() { return startAt; }
    public LocalDateTime getEndAt() { return endAt; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public LocalDateTime getHoldExpiresAt() { return holdExpiresAt; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
}
