package com.example.domain.notification.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

public record NotificationPostedEvent(
    String aggregateId,
    String channel,
    String body,
    Map<String, String> metadata,
    Instant occurredAt
) implements DomainEvent {
    @Override public String type() { return "NotificationPosted"; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
