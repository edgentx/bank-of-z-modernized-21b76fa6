package com.example.domain.legacybridge.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public class RoutingUpdatedEvent implements DomainEvent {
    private final String eventId = UUID.randomUUID().toString();
    private final String aggregateId;
    private final String ruleId;
    private final String newTarget;
    private final int ruleVersion;
    private final Instant effectiveDate;
    private final Instant occurredAt;

    public RoutingUpdatedEvent(String aggregateId, String ruleId, String newTarget, int ruleVersion, Instant effectiveDate) {
        this.aggregateId = aggregateId;
        this.ruleId = ruleId;
        this.newTarget = newTarget;
        this.ruleVersion = ruleVersion;
        this.effectiveDate = effectiveDate;
        this.occurredAt = Instant.now();
    }

    @Override
    public String type() {
        return "routing.updated";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String ruleId() { return ruleId; }
    public String newTarget() { return newTarget; }
    public int ruleVersion() { return ruleVersion; }
    public Instant effectiveDate() { return effectiveDate; }
}
