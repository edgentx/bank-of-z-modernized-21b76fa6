package com.example.domain.notification.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record NotificationSentEvent(
    String notificationId,
    String channel,
    String target,
    String formattedBody,
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
}
