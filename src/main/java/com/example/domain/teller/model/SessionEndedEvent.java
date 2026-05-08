package com.example.domain.teller.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;

/**
 * Event published when a TellerSession is terminated.
 */
public record SessionEndedEvent(
        String aggregateId,
        String type,
        Instant occurredAt
) implements DomainEvent {}
