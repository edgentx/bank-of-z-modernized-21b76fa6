package com.example.domain.tellersession.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record SessionEndedEvent(
    String type,
    String aggregateId,
    Instant occurredAt
) implements DomainEvent {}
