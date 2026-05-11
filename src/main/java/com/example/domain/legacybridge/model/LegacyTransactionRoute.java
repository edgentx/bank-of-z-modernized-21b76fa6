package com.example.domain.legacybridge.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

public class LegacyTransactionRoute extends AggregateRoot {

    private final String routeId;
    private int ruleVersion;
    private String currentTarget;
    private boolean isDualProcessingEnabled; // Simulated invariant state

    public LegacyTransactionRoute(String routeId) {
        this.routeId = routeId;
        this.ruleVersion = 1; // Default version
        this.isDualProcessingEnabled = false;
    }

    @Override
    public String id() {
        return routeId;
    }

    // Method to set up test state for invariants
    public void markDualProcessingViolation(boolean violation) {
        this.isDualProcessingEnabled = violation;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EvaluateRoutingCmd c) {
            return evaluateRouting(c);
        } else if (cmd instanceof UpdateRoutingRuleCmd c) {
            return updateRoutingRule(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> updateRoutingRule(UpdateRoutingRuleCmd cmd) {
        // Invariant: A transaction must route to exactly one backend system
        // If dual processing is enabled (simulated state), reject the update.
        if (this.isDualProcessingEnabled) {
            throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }

        // Invariant: Routing rules must be versioned to allow safe rollback.
        // We enforce this by incrementing the version, but we need a valid starting point.
        if (this.ruleVersion <= 0) {
            throw new IllegalStateException("Routing rules must be versioned to allow safe rollback.");
        }

        // Business Logic: Update target
        this.currentTarget = cmd.newTarget();
        this.ruleVersion++; // Increment version for the update

        var event = new RoutingRuleUpdatedEvent(
                this.routeId,
                cmd.ruleId(),
                cmd.newTarget(),
                cmd.effectiveDate(),
                Instant.now()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private List<DomainEvent> evaluateRouting(EvaluateRoutingCmd cmd) {
        // Existing logic stub (preserved for compilation, though S-24 focuses on UpdateRoutingRuleCmd)
        if (cmd.rulesVersion() <= 0) {
            throw new IllegalArgumentException("Routing rules must be versioned to allow safe rollback.");
        }
        return List.of();
    }

    public String getCurrentTarget() {
        return currentTarget;
    }
}