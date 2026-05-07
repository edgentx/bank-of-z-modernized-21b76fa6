package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record DefectReportedEvent(
    String aggregateId,
    String title,
    String description,
    Instant occurredAt
) implements DomainEvent {
    public DefectReportedEvent(String id, String title, String desc) {
        this(id, title, desc, Instant.now());
    }

    @Override
    public String type() {
        return "DefectReported";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
