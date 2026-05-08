package com.example.domain.notification.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record NotificationSentEvent(
    String notificationId,
    String slackBody,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "NotificationSent";
    }

    @Override
    public String aggregateId() {
        return notificationId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
