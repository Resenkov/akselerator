package ru.dstu.work.akselerator.handler;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.dstu.work.akselerator.dto.WarningInfo;
import ru.dstu.work.akselerator.exception.QuotaExceededException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(QuotaExceededException.class)
    public ResponseEntity<WarningInfo> handleQuota(QuotaExceededException ex) {
        WarningInfo w = ex.getWarning();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(w);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);
        body.put("error", "Validation failed");
        body.put("message", ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleIntegrity(DataIntegrityViolationException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 400);
        body.put("error", "Data integrity violation");
        String message = resolveIntegrityMessage(ex);
        if (message != null) {
            body.put("message", message);
        }
        return ResponseEntity.badRequest().body(body);
    }

    private String resolveIntegrityMessage(DataIntegrityViolationException ex) {
        Throwable root = ex;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        String rootMessage = root.getMessage();
        if (rootMessage == null) {
            return null;
        }

        if (rootMessage.contains("allocation_quotas_organization_id_species_id_region_id_peri_key")
                || rootMessage.contains("uq_quota_org_species_region_period")) {
            return "Квота с таким сочетанием организации, вида рыбы, региона и периода уже существует.";
        }

        if (rootMessage.contains("duplicate key value")) {
            return "Запись с такими данными уже существует.";
        }

        return rootMessage;
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<Map<String, Object>> handleDenied(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 403);
        body.put("error", "Forbidden");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOther(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 500);
        body.put("error", "Internal Server Error");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
