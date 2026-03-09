package ru.dstu.work.akselerator.parkingaccount;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParkingAccountServiceTest {
    private MutableClock clock;
    private ParkingAccountService service;

    @BeforeEach
    void setUp() {
        clock = new MutableClock(Instant.parse("2026-01-01T10:00:00Z"), ZoneOffset.UTC);
        service = new ParkingAccountService(clock, new BigDecimal("100.00"));
        service.topUp("u1", new BigDecimal("1000.00"), UUID.randomUUID());
    }

    @Test
    void holdExpireAndRefundFull() {
        Booking booking = service.createHold(
                "u1",
                LocalDateTime.of(2026, 1, 1, 12, 0),
                LocalDateTime.of(2026, 1, 1, 13, 0),
                UUID.randomUUID());

        clock.plusSeconds(6 * 60);
        service.expireHold(booking.getId(), UUID.randomUUID());

        assertEquals(BookingStatus.EXPIRED, service.getBooking(booking.getId()).getStatus());
        assertEquals(new BigDecimal("1000.00"), service.getAccount("u1").getAvailable());
    }

    @Test
    void noShowGetsZeroRefund() {
        Booking booking = service.createHold(
                "u1",
                LocalDateTime.of(2026, 1, 1, 10, 30),
                LocalDateTime.of(2026, 1, 1, 11, 0),
                UUID.randomUUID());
        service.confirm(booking.getId());

        clock.plusSeconds(46 * 60);
        service.markNoShow(booking.getId(), UUID.randomUUID());

        assertEquals(BookingStatus.NO_SHOW, service.getBooking(booking.getId()).getStatus());
        assertEquals(new BigDecimal("800.00"), service.getAccount("u1").getAvailable());
    }

    @Test
    void cancelBefore60MinRefund100() {
        Booking booking = service.createHold(
                "u1",
                LocalDateTime.of(2026, 1, 1, 14, 0),
                LocalDateTime.of(2026, 1, 1, 15, 0),
                UUID.randomUUID());

        service.cancel(booking.getId(), UUID.randomUUID());
        assertEquals(new BigDecimal("1000.00"), service.getAccount("u1").getAvailable());
    }

    @Test
    void bookingValidationWorks() {
        assertThrows(IllegalArgumentException.class, () -> service.createHold(
                "u1",
                LocalDateTime.of(2026, 1, 9, 10, 1),
                LocalDateTime.of(2026, 1, 9, 10, 31),
                UUID.randomUUID()));
    }

    @Test
    void idempotencyByOperationId() {
        UUID operationId = UUID.randomUUID();
        service.topUp("u1", new BigDecimal("100.00"), operationId);
        service.topUp("u1", new BigDecimal("100.00"), operationId);

        assertEquals(new BigDecimal("1100.00"), service.getAccount("u1").getAvailable());
    }
}
