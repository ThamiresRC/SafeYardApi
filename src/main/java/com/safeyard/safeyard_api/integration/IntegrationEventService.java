package com.safeyard.safeyard_api.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IntegrationEventService {

    private final IntegrationEventRepository repository;
    private final ObjectMapper objectMapper;

    public Long save(String source, String type, String tsIso, Map<String, Object> data) {
        LocalDateTime eventUtc = parseToUtc(tsIso);

        String json = null;
        try {
            if (data != null) {
                json = objectMapper.writeValueAsString(data);
            }
        } catch (JsonProcessingException ignored) {
            // Em último caso, podemos salvar nulo — ou trocar por um JSON de fallback.
        }

        IntegrationEventEntity e = IntegrationEventEntity.builder()
                .source(source)
                .type(type)
                .eventTs(eventUtc)
                .data(json)
                .build();

        return repository.save(e).getId();
    }

    public Page<IntegrationEventEntity> list(String typeFilter, Pageable pageable) {
        if (typeFilter != null && !typeFilter.isBlank()) {
            return repository.findByTypeContainingIgnoreCase(typeFilter, pageable);
        }
        return repository.findAll(pageable);
    }

    /**
     * Tenta interpretar tsIso como OffsetDateTime (com fuso). Se falhar,
     * tenta como LocalDateTime (sem fuso) assumindo timezone local do servidor,
     * e converte sempre para UTC.
     */
    private LocalDateTime parseToUtc(String tsIso) {
        if (tsIso == null || tsIso.isBlank()) return null;

        try {
            // Ex.: "2025-10-28T10:00:00-03:00"
            return OffsetDateTime.parse(tsIso)
                    .withOffsetSameInstant(ZoneOffset.UTC)
                    .toLocalDateTime();
        } catch (Exception ignore) {
            // Sem offset? Tenta como "2025-10-28T10:00:00" (assume timezone local do servidor)
            try {
                LocalDateTime local = LocalDateTime.parse(tsIso);
                ZoneId zone = ZoneId.systemDefault(); // ou ZoneId.of("America/Sao_Paulo")
                return local.atZone(zone)
                        .withZoneSameInstant(ZoneOffset.UTC)
                        .toLocalDateTime();
            } catch (Exception ignoredToo) {
                // Se nada der certo, devolve null (campo ficará nulo)
                return null;
            }
        }
    }
}
