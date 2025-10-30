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

    private LocalDateTime parseToUtc(String tsIso) {
        if (tsIso == null || tsIso.isBlank()) return null;

        try {
            return OffsetDateTime.parse(tsIso)
                    .withOffsetSameInstant(ZoneOffset.UTC)
                    .toLocalDateTime();
        } catch (Exception ignore) {
            try {
                LocalDateTime local = LocalDateTime.parse(tsIso);
                ZoneId zone = ZoneId.systemDefault();
                return local.atZone(zone)
                        .withZoneSameInstant(ZoneOffset.UTC)
                        .toLocalDateTime();
            } catch (Exception ignoredToo) {
                return null;
            }
        }
    }
}
