package com.safeyard.safeyard_api.integration;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntegrationEventRepository extends JpaRepository<IntegrationEventEntity, Long> {
    Page<IntegrationEventEntity> findByTypeContainingIgnoreCase(String type, Pageable pageable);
}
