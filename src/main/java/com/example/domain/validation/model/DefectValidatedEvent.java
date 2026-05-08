package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record DefectValidatedEvent(
    String aggregateId,
    String githubIssueUrl,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "DefectValidated";
    }
    @Override
    public String aggregateId() {
        return aggregateId;
    }
}
