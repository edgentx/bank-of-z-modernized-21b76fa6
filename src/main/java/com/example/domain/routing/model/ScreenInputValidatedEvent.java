package com.example.domain.routing.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

public class ScreenInputValidatedEvent implements DomainEvent {
    private final String aggregateId;
    private final String screenId;
    private final Map<String, String> inputFields;
    private final Instant occurredAt;

    public ScreenInputValidatedEvent(String aggregateId, String screenId, Map<String, String> inputFields, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.screenId = screenId;
        this.inputFields = inputFields;
        this.occurredAt = occurredAt;
    }

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

    public String screenId() { return screenId; }
    public Map<String, String> inputFields() { return inputFields; }
}
