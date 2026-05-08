package com.example.domain.legacybridge.model;

import com.example.domain.legacybridge.command.UpdateRoutingRuleCmd;
import com.example.domain.legacybridge.event.RoutingUpdatedEvent;
import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

/**
 * Aggregate Root for Legacy Transaction Routing.
 * Consolidated implementation for S-24.
 */
public class LegacyTransactionRoute extends AggregateRoot {

    private final String routeId;
    private int currentRuleVersion;
    private String currentTarget;

    // Test flags for simulating invariant violations
    private boolean violateSingleTarget;
    private boolean violateVersioning;

    public LegacyTransactionRoute(String routeId) {
        this.routeId = routeId;
        this.currentRuleVersion = 1; // Default initial state
        this.currentTarget = "CICS"; // Default legacy target
    }

    @Override
    public String id() {
        return routeId;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof UpdateRoutingRuleCmd c) {
            return updateRoutingRule(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> updateRoutingRule(UpdateRoutingRuleCmd cmd) {
        // Invariant Check: Dual-processing / Single Target
        if (violateSingleTarget) {
            throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }
        // Validation: Target must be explicit
        if (cmd.newTarget() == null || cmd.newTarget().isBlank()) {
            throw new IllegalArgumentException("newTarget must be specified.");
        }

        // Invariant Check: Versioning
        if (violateVersioning) {
            throw new IllegalStateException("Routing rules must be versioned to allow safe rollback.");
        }
        if (cmd.newRuleVersion() <= 0) {
            throw new IllegalArgumentException("Routing rules must be versioned to allow safe rollback (version must be > 0).");
        }

        // Apply state changes
        this.currentTarget = cmd.newTarget();
        this.currentRuleVersion = cmd.newRuleVersion();

        var event = new RoutingUpdatedEvent(
                null, // eventId generated in constructor
                this.routeId,
                cmd.ruleId(),
                cmd.newTarget(),
                cmd.effectiveDate(),
                cmd.newRuleVersion(),
                Instant.now()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // Test Helper Methods
    public void markDualProcessingViolation() {
        this.violateSingleTarget = true;
    }

    public void markVersioningViolation() {
        this.violateVersioning = true;
    }

    public String getCurrentTarget() {
        return currentTarget;
    }

    public int getCurrentRuleVersion() {
        return currentRuleVersion;
    }
}
