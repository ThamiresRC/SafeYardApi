package com.safeyard.safeyard_api.integration;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "integration_events")
public class IntegrationEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String source;

    @Column(nullable = false, length = 80)
    private String type;

    @Column(name = "event_ts")
    private LocalDateTime eventTs;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String data;
}
