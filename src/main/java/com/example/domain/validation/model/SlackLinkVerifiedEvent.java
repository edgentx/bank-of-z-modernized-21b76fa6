package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

public record SlackLinkVerifiedEvent(String validationId, boolean found, Instant occurredAt) implements DomainEvent {
    @Override public String type() { return "SlackLinkVerified"; }
    @Override public String aggregateId() { return validationId; }
    @Override public Instant occurredAt() { return occurredAt; }
}
