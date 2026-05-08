package com.example.domain.routing.model;

import com.example.domain.shared.DomainEvent;

import java.time.Instant;
import java.util.Map;

public class RoutingEvaluatedEvent implements DomainEvent {
    private final String aggregateId;
    private final String transactionType;
    private final String targetSystem;
    private final String payload;
    private final int ruleVersion;
    private final Instant occurredAt;

    public RoutingEvaluatedEvent(String aggregateId, String transactionType, String targetSystem, String payload, int ruleVersion, Instant occurredAt) {
        this.aggregateId = aggregateId;
        this.transactionType = transactionType;
        this.targetSystem = targetSystem;
        this.payload = payload;
        this.ruleVersion = ruleVersion;
        this.occurredAt = occurredAt;
    }

    @Override
    public String type() {
        return "routing.evaluated";
    }

    @Override
    public String aggregateId() {
        return aggregateId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    public String transactionType() {
        return transactionType;
    }

    public String targetSystem() {
        return targetSystem;
    }

    public String payload() {
        return payload;
    }

    public int ruleVersion() {
        return ruleVersion;
    }
}