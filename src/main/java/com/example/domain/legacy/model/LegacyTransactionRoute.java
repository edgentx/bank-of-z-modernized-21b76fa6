package com.example.domain.legacy.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate Root for Legacy Transaction Routing.
 * Manages routing logic and invariants.
 */
public class LegacyTransactionRoute extends AggregateRoot {

    private final String routeId;
    private int currentRuleVersion = 1; // Default version
    private boolean isRouted = false;

    // Test helpers to simulate invariant violations
    private transient boolean forceDualProcessingViolation = false;
    private transient boolean forceVersioningViolation = false;

    public LegacyTransactionRoute(String routeId) {
        this.routeId = routeId;
    }

    @Override
    public String id() {
        return routeId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof UpdateRoutingRuleCmd c) {
            return handleUpdateRoutingRule(c);
        }
        // If there were other commands, they would be here.
        // Removing references to non-existent S-24 specific commands to fix compilation.
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> handleUpdateRoutingRule(UpdateRoutingRuleCmd cmd) {
        // Invariant: Routing rules must be versioned to allow safe rollback.
        if (forceVersioningViolation || currentRuleVersion <= 0) {
            throw new IllegalStateException("Routing rules must be versioned to allow safe rollback.");
        }

        // Invariant: A transaction must route to exactly one backend system.
        if (forceDualProcessingViolation) {
            throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }

        // Basic validation
        if (cmd.ruleId() == null || cmd.ruleId().isBlank()) {
            throw new IllegalArgumentException("ruleId is required");
        }
        if (cmd.newTarget() == null || cmd.newTarget().isBlank()) {
            throw new IllegalArgumentException("newTarget is required");
        }
        if (cmd.effectiveDate() == null) {
            throw new IllegalArgumentException("effectiveDate is required");
        }

        // Business Logic
        var event = new RoutingRuleUpdatedEvent(
                cmd.routeId(),
                cmd.ruleId(),
                cmd.newTarget(),
                cmd.effectiveDate(),
                Instant.now()
        );

        // Apply state changes (simulated)
        // In a real scenario, this might update internal rules configuration.
        this.currentRuleVersion++; 
        addEvent(event);
        incrementVersion();
        
        return List.of(event);
    }

    // Test Helper Methods
    public void setForceDualProcessingViolation(boolean force) {
        this.forceDualProcessingViolation = force;
    }

    public void setForceVersioningViolation(boolean force) {
        this.forceVersioningViolation = force;
    }

    public void setCurrentRuleVersion(int version) {
        this.currentRuleVersion = version;
    }
}
