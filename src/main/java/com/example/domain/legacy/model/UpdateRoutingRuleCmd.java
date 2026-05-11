package com.example.domain.legacy.model;

import com.example.domain.shared.Command;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class UpdateRoutingRuleCmd implements Command {

    private final UUID aggregateId;
    private final String ruleId;
    private final String newTarget;
    private final LocalDate effectiveDate;

    public UpdateRoutingRuleCmd(UUID aggregateId, String ruleId, String newTarget, LocalDate effectiveDate) {
        this.aggregateId = Objects.requireNonNull(aggregateId, "Aggregate ID cannot be null");
        this.ruleId = Objects.requireNonNull(ruleId, "Rule ID cannot be null");
        this.newTarget = Objects.requireNonNull(newTarget, "New Target cannot be null");
        this.effectiveDate = Objects.requireNonNull(effectiveDate, "Effective Date cannot be null");
    }

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
}
