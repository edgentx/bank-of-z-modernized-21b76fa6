package com.example.domain.notification.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record SlackNotificationSentEvent(
    String aggregateId,
    String channel,
    String messageBody,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "SlackNotificationSent";
    }

    @Override
    public String aggregateId() {
        return aggregateId();
    }

    @Override
    public Instant occurredAt() {
        return occurredAt();
    }
}
