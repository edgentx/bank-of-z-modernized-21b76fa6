package com.example.domain.notification.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record NotificationPreparedEvent(
    String notificationId,
    String channel,
    String messageBody,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "NotificationPrepared";
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