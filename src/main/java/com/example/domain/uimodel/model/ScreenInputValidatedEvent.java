package com.example.domain.uimodel.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.Map;

public class ScreenInputValidatedEvent implements DomainEvent {
    private final String screenId;
    private final Map<String, String> inputFields;
    private final Instant occurredAt;

    public ScreenInputValidatedEvent(String screenId, Map<String, String> inputFields, Instant occurredAt) {
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
        return screenId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public Map<String, String> getInputFields() {
        return inputFields;
    }
}
