package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record GitHubIssueLinkedEvent(
    String aggregateId,
    String issueUrl,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "GitHubIssueLinked";
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
