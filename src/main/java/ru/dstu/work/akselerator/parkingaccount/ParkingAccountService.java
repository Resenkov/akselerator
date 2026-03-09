package ru.dstu.work.akselerator.parkingaccount;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class ParkingAccountService {
    private final Clock clock;
    private final BigDecimal slotPrice;

    private final Map<String, Account> accounts = new HashMap<>();
    private final Map<UUID, Booking> bookings = new HashMap<>();
    private final List<AccountOperation> operations = new ArrayList<>();
    private final Set<UUID> processedOperationIds = new HashSet<>();

    public ParkingAccountService(Clock clock, BigDecimal slotPrice) {
        this.clock = Objects.requireNonNull(clock);
        this.slotPrice = slotPrice.setScale(2, RoundingMode.HALF_UP);
    }

    public Booking createHold(String userId, LocalDateTime startAt, LocalDateTime endAt, UUID operationId) {
        LocalDateTime now = now();
        validateBookingWindow(now, startAt, endAt);

        long slots = Duration.between(startAt, endAt).toMinutes() / 15;
        BigDecimal total = slotPrice.multiply(BigDecimal.valueOf(slots)).setScale(2, RoundingMode.HALF_UP);

        Booking booking = new Booking(UUID.randomUUID(), userId, startAt, endAt, total, now);
        bookings.put(booking.getId(), booking);

        holdFunds(userId, booking.getId(), total, operationId);
        return booking;
    }

    public void confirm(UUID bookingId) {
        Booking booking = getBooking(bookingId);
        ensureStatus(booking, BookingStatus.HOLD);
        booking.setStatus(BookingStatus.CONFIRMED);
    }

    public void activate(UUID bookingId, UUID captureOperationId) {
        Booking booking = getBooking(bookingId);
        ensureStatus(booking, BookingStatus.CONFIRMED);
        booking.setStatus(BookingStatus.ACTIVE);
        captureFunds(booking.getUserId(), booking.getId(), booking.getTotalAmount(), captureOperationId);
    }

    public void complete(UUID bookingId) {
        Booking booking = getBooking(bookingId);
        ensureStatus(booking, BookingStatus.ACTIVE);
        booking.setStatus(BookingStatus.COMPLETED);
    }

    public void cancel(UUID bookingId, UUID refundOperationId) {
        Booking booking = getBooking(bookingId);
        if (booking.getStatus() == BookingStatus.ACTIVE || booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Активную/завершенную бронь нельзя отменить");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.EXPIRED || booking.getStatus() == BookingStatus.NO_SHOW) {
            return;
        }

        LocalDateTime now = now();
        BigDecimal refundRatio = cancellationRatio(booking, now);
        booking.setStatus(BookingStatus.CANCELLED);
        refundWithPenalty(booking, refundRatio, refundOperationId);
    }

    public void expireHold(UUID bookingId, UUID refundOperationId) {
        Booking booking = getBooking(bookingId);
        if (booking.getStatus() != BookingStatus.HOLD) {
            return;
        }
        if (!now().isAfter(booking.getHoldExpiresAt())) {
            throw new IllegalStateException("HOLD еще не истек");
        }
        booking.setStatus(BookingStatus.EXPIRED);
        refundWithPenalty(booking, BigDecimal.ONE, refundOperationId);
    }

    public void markNoShow(UUID bookingId, UUID penaltyOperationId) {
        Booking booking = getBooking(bookingId);
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            return;
        }
        if (!now().isAfter(booking.getStartAt().plus(Booking.ARRIVAL_WINDOW))) {
            throw new IllegalStateException("Окно прибытия еще не закончено");
        }
        booking.setStatus(BookingStatus.NO_SHOW);
        releaseHoldAsPenalty(booking.getUserId(), booking.getId(), booking.getTotalAmount(), penaltyOperationId);
    }

    public void topUp(String userId, BigDecimal amount, UUID operationId) {
        if (!processedOperationIds.add(operationId)) {
            return;
        }
        Account account = accounts.computeIfAbsent(userId, Account::new);
        account.increaseAvailable(amount);
        operations.add(new AccountOperation(operationId, userId, null, OperationType.TOP_UP, amount, now()));
    }

    public Booking getBooking(UUID bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Бронь не найдена: " + bookingId);
        }
        return booking;
    }

    public Account getAccount(String userId) {
        return accounts.computeIfAbsent(userId, Account::new);
    }

    public List<AccountOperation> getOperations() {
        return List.copyOf(operations);
    }

    private void validateBookingWindow(LocalDateTime now, LocalDateTime startAt, LocalDateTime endAt) {
        if (!startAt.isAfter(now)) {
            throw new IllegalArgumentException("Бронь должна начинаться в будущем");
        }
        Duration duration = Duration.between(startAt, endAt);
        if (duration.isNegative() || duration.isZero()) {
            throw new IllegalArgumentException("Некорректная длительность");
        }
        if (duration.toMinutes() % Booking.SLOT_STEP.toMinutes() != 0) {
            throw new IllegalArgumentException("Длительность должна быть кратна 15 минутам");
        }
        if (duration.compareTo(Booking.MAX_DURATION) > 0) {
            throw new IllegalArgumentException("Максимум 12 часов за раз");
        }
        if (Duration.between(now, startAt).compareTo(Booking.MAX_AHEAD) > 0) {
            throw new IllegalArgumentException("Бронирование вперед максимум на 7 дней");
        }
    }

    private BigDecimal cancellationRatio(Booking booking, LocalDateTime now) {
        if (now.isBefore(booking.getStartAt().minusMinutes(60))) {
            return new BigDecimal("1.00");
        }
        if (now.isBefore(booking.getStartAt().minusMinutes(15))) {
            return new BigDecimal("0.80");
        }
        if (now.isBefore(booking.getStartAt())) {
            return new BigDecimal("0.60");
        }
        return new BigDecimal("0.30");
    }

    private void holdFunds(String userId, UUID bookingId, BigDecimal amount, UUID operationId) {
        if (!processedOperationIds.add(operationId)) {
            return;
        }
        Account account = accounts.computeIfAbsent(userId, Account::new);
        if (account.getAvailable().compareTo(amount) < 0) {
            throw new IllegalStateException("Недостаточно средств для HOLD");
        }
        account.decreaseAvailable(amount);
        account.increaseFrozen(amount);
        operations.add(new AccountOperation(operationId, userId, bookingId, OperationType.HOLD, amount, now()));
    }

    private void captureFunds(String userId, UUID bookingId, BigDecimal amount, UUID operationId) {
        if (!processedOperationIds.add(operationId)) {
            return;
        }
        Account account = accounts.computeIfAbsent(userId, Account::new);
        account.decreaseFrozen(amount);
        operations.add(new AccountOperation(operationId, userId, bookingId, OperationType.CAPTURE, amount, now()));
    }

    private void refundWithPenalty(Booking booking, BigDecimal refundRatio, UUID refundOperationId) {
        Account account = accounts.computeIfAbsent(booking.getUserId(), Account::new);
        BigDecimal amount = booking.getTotalAmount();
        BigDecimal refund = amount.multiply(refundRatio).setScale(2, RoundingMode.HALF_UP);
        BigDecimal penalty = amount.subtract(refund).setScale(2, RoundingMode.HALF_UP);

        if (refund.compareTo(BigDecimal.ZERO) > 0) {
            registerOnce(refundOperationId, booking.getUserId(), booking.getId(), OperationType.REFUND, refund, () -> {
                account.decreaseFrozen(amount);
                account.increaseAvailable(refund);
            });
        } else {
            account.decreaseFrozen(amount);
        }

        if (penalty.compareTo(BigDecimal.ZERO) > 0) {
            registerOnce(UUID.randomUUID(), booking.getUserId(), booking.getId(), OperationType.PENALTY, penalty, () -> {
            });
        }
    }

    private void releaseHoldAsPenalty(String userId, UUID bookingId, BigDecimal amount, UUID operationId) {
        registerOnce(operationId, userId, bookingId, OperationType.PENALTY, amount, () -> {
            Account account = accounts.computeIfAbsent(userId, Account::new);
            account.decreaseFrozen(amount);
        });
    }

    private void registerOnce(UUID operationId, String userId, UUID bookingId, OperationType type, BigDecimal amount, Runnable updateBalance) {
        if (!processedOperationIds.add(operationId)) {
            return;
        }
        updateBalance.run();
        operations.add(new AccountOperation(operationId, userId, bookingId, type, amount, now()));
    }

    private void ensureStatus(Booking booking, BookingStatus expected) {
        if (booking.getStatus() != expected) {
            throw new IllegalStateException("Ожидался статус " + expected + ", фактический: " + booking.getStatus());
        }
    }

    private LocalDateTime now() {
        return LocalDateTime.now(clock);
    }
}
