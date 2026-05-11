package com.example.domain.routing.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Domain event emitted when screen input is successfully validated against the BMS map constraints.
 */
public record InputValidatedEvent(
    String aggregateId,
    String screenId,
    Map<String, String> inputFields,
    Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "input.validated";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InputValidatedEvent that = (InputValidatedEvent) o;
        return Objects.equals(aggregateId, that.aggregateId) &&
                Objects.equals(screenId, that.screenId) &&
                Objects.equals(inputFields, that.inputFields) &&
                Objects.equals(occurredAt, that.occurredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateId, screenId, inputFields, occurredAt);
    }
}
