package com.example.domain.legacy.model;

import com.example.domain.shared.DomainEvent;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class RoutingRuleUpdatedEvent implements DomainEvent {

    private final UUID eventId;
    private final UUID aggregateId;
    private final String ruleId;
    private final String newTarget;
    private final LocalDate effectiveDate;
    private final int newVersion;

    public RoutingRuleUpdatedEvent(UUID aggregateId, String ruleId, String newTarget, LocalDate effectiveDate, int newVersion) {
        this.eventId = UUID.randomUUID();
        this.aggregateId = aggregateId;
        this.ruleId = ruleId;
        this.newTarget = newTarget;
        this.effectiveDate = effectiveDate;
        this.newVersion = newVersion;
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public UUID getAggregateId() {
        return aggregateId;
    }

    public String getRuleId() {
        return ruleId;
    }

    public String getNewTarget() {
        return newTarget;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public int getNewVersion() {
        return newVersion;
    }

    @Override
    public String toString() {
        return "RoutingRuleUpdatedEvent{" +
                "eventId=" + eventId +
                ", aggregateId=" + aggregateId +
                ", ruleId='" + ruleId + '\'' +
                ", newTarget='" + newTarget + '\'' +
                ", effectiveDate=" + effectiveDate +
                ", newVersion=" + newVersion +
                '}';
    }
}
