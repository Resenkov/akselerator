package ru.dstu.work.akselerator.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.dstu.work.akselerator.exception.QuotaExceededException;
import ru.dstu.work.akselerator.dto.WarningInfo;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(QuotaExceededException.class)
    public ResponseEntity<?> handleQuotaExceeded(QuotaExceededException ex) {
        WarningInfo w = ex.getWarning();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(w);
    }
}
