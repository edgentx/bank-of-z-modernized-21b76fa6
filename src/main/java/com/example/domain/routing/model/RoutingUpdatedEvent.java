package com.example.domain.routing.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public class RoutingUpdatedEvent implements DomainEvent {
    private final String aggregateId;
    private final String ruleId;
    private final String newTarget;
    private final Instant effectiveDate;
    private final Instant occurredAt;

    public RoutingUpdatedEvent(String aggregateId, String ruleId, String newTarget, Instant effectiveDate, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.ruleId = ruleId;
        this.newTarget = newTarget;
        this.effectiveDate = effectiveDate;
        this.occurredAt = occurredAt;
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
    public Instant effectiveDate() { return effectiveDate; }
}
