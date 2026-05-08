package com.example.domain.validation;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record ValidationReportedEvent(
    String aggregateId,
    String description,
    String severity,
    String component,
    String ticketUrl,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "ValidationReported";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }
}