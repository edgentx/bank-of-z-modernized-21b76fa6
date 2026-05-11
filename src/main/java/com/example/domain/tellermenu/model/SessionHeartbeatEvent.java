package com.example.domain.tellermenu.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public record SessionHeartbeatEvent(String aggregateId, Instant occurredAt) implements DomainEvent {
    @Override public String type() { return "session.heartbeat"; }
    @Override public String aggregateId() { return aggregateId; }
    @Override public Instant occurredAt() { return occurredAt; }
}