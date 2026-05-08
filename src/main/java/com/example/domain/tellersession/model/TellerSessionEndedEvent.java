package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record TellerSessionEndedEvent(
    String aggregateId,
    Instant occurredAt
) implements DomainEvent {
    @Override
    public String type() {
        return "teller.session.ended";
    }
}