package com.example.domain.vforce360.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

public record DefectReportedEvent(
        String type,
        String aggregateId,
        Instant occurredAt,
        String title,
        String description,
        String reporter
) implements DomainEvent {
    public DefectReportedEvent(String aggregateId, String title, String description, String reporter) {
        this("DefectReported", aggregateId, Instant.now(), title, description, reporter);
    }
}