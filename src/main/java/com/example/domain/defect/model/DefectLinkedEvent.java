package com.example.domain.defect.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record DefectLinkedEvent(
    String aggregateId,
    String githubUrl,
    Instant occurredAt
) implements DomainEvent {
    @Override public String type() { return "DefectLinked"; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
}