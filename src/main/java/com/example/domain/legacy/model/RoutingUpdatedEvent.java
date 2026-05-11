package com.example.domain.legacy.model;

import com.example.domain.shared.DomainEvent;
import java.time.Instant;

public class RoutingUpdatedEvent implements DomainEvent {
    private final String aggregateId;
    private final String ruleId;
    private final String target;
    private final Instant effectiveDate;
    private final Instant occurredAt;

    public RoutingUpdatedEvent(String aggregateId, String ruleId, String target, Instant effectiveDate, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.ruleId = ruleId;
        this.target = target;
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
    public String target() { return target; }
    public Instant effectiveDate() { return effectiveDate; }
}
