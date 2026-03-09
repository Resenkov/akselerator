package ru.dstu.work.akselerator.parkingaccount;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AccountOperation(
        UUID operationId,
        String userId,
        UUID bookingId,
        OperationType type,
        BigDecimal amount,
        LocalDateTime createdAt
) {
}
