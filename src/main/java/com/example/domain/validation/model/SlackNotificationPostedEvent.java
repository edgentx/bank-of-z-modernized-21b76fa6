package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Event indicating a notification has been posted to the #vforce360-issues Slack channel.
 * Used for validating VW-454 regression.
 */
public record SlackNotificationPostedEvent(
    String aggregateId,
    String channel,
    String body,
    Instant occurredAt
) implements DomainEvent {
    
    @Override
    public String type() {
        return "SlackNotificationPosted";
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
