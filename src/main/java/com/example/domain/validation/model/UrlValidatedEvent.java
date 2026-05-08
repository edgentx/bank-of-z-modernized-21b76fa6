package com.example.domain.validation.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record UrlValidatedEvent(
    String validationId,
    String textContent,
    String requiredUrl,
    Instant occurredAt
) implements DomainEvent {

    @Override
    public String type() {
        return "UrlValidated";
    }

    @Override
    public String aggregateId() {
        return validationId;
    }
}
