package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

// Record implementing DomainEvent
public record SessionEndedEvent(
    String aggregateId,
    String type,
    Instant occurredAt
) implements DomainEvent {}
