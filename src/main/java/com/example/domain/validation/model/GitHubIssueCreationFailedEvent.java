package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event emitted when an attempt to create a GitHub issue fails.
 */
public record GitHubIssueCreationFailedEvent(
        String aggregateId,
        String reason,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "GitHubIssueCreationFailed";
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