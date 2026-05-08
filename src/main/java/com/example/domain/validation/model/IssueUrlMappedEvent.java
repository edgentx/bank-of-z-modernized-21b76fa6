package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record IssueUrlMappedEvent(
    String aggregateId,
    String url,
    Instant mappedAt
) implements DomainEvent {
    @Override public String type() { return "IssueUrlMapped"; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return mappedAt; }
}