package com.safeyard.safeyard_api.api;

import com.safeyard.safeyard_api.integration.IntegrationEventEntity;
import com.safeyard.safeyard_api.integration.IntegrationEventService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/integrations")
@RequiredArgsConstructor
public class IntegrationController {

    private final IntegrationEventService service;

    @Value("${spring.application.name:safeyard-api}")
    private String appName;

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "service", appName,
                "status", "UP",
                "time", OffsetDateTime.now().toString()
        );
    }

    @PostMapping("/events")
    public ResponseEntity<Map<String, Object>> receiveEvent(@RequestBody IntegrationEvent event) {
        String ts = (event.getTimestamp() == null || event.getTimestamp().isBlank())
                ? OffsetDateTime.now().toString()
                : event.getTimestamp();

        Map<String, Object> dataToSave;
        Object payload = event.getData();
        if (payload instanceof Map<?, ?> map) {
            dataToSave = (Map<String, Object>) map;
        } else {
            dataToSave = Map.of("value", payload);
        }

        log.info("[INTEGRATION EVENT] source={}, type={}, ts={}, data={}",
                event.getSource(), event.getType(), ts, payload);

        Long id = service.save(event.getSource(), event.getType(), ts, dataToSave);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Map.of(
                        "received", true,
                        "id", id,
                        "type", event.getType(),
                        "timestamp", ts
                ));
    }

    @GetMapping("/events")
    public Page<IntegrationEventEntity> listEvents(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.list(type, PageRequest.of(page, size));
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleBadJson(HttpMessageNotReadableException ex) {
        log.warn("JSON inválido no /api/integrations/events: {}", ex.getMostSpecificCause().getMessage());
        return ResponseEntity.badRequest().body(Map.of(
                "status", 400,
                "error", "Bad Request",
                "message", "JSON inválido: " + ex.getMostSpecificCause().getMessage(),
                "timestamp", OffsetDateTime.now().toString()
        ));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDb(DataAccessException ex) {
        log.error("Erro de banco ao gravar IntegrationEvent", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", 500,
                "error", "Internal Server Error",
                "message", "Falha ao persistir o evento",
                "timestamp", OffsetDateTime.now().toString()
        ));
    }

    @Data
    public static class IntegrationEvent {
        private String source;
        private String type;
        private String timestamp;
        private Object data;
    }
}
