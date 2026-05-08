package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Domain event emitted when a defect report is successfully posted to Slack.
 */
public record SlackNotificationPostedEvent(
    String aggregateId,
    String slackChannelId,
    String messageBody,
    Instant occurredAt
) implements DomainEvent {
    @Override public String type() { return "SlackNotificationPosted"; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
