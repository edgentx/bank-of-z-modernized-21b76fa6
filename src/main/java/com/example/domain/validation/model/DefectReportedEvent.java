package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record DefectReportedEvent(
    String aggregateId,
    String githubUrl,
    String severity,
    String component,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "DefectReported";
    }
    
    // Constructor wrapper to generate ID if needed, though record handles it
    public static DefectReportedEvent create(String id, String url, String severity, String component) {
        return new DefectReportedEvent(id, url, severity, component, Instant.now());
    }
}
