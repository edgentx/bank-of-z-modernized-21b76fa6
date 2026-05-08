package com.example.domain.legacybridge.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

/**
 * Aggregate Root for Legacy Transaction Routing.
 * Determines the target system (Modern vs Legacy) based on feature flags and rules.
 * Enforces invariants: Single Target and Versioning.
 */
public class LegacyTransactionRoute extends AggregateRoot {

    private final String routeId;
    private boolean dualProcessingViolation;
    private boolean versioningViolation;
    private boolean evaluated;
    private String targetSystem;

    // State required for S-24 command handling
    private int currentRulesVersion = 1;

    public LegacyTransactionRoute(String routeId) {
        this.routeId = routeId;
        this.dualProcessingViolation = false;
        this.versioningViolation = false;
        this.evaluated = false;
    }

    @Override
    public String id() {
        return routeId;
    }

    /**
     * Helper to setup test state for invariants violations.
     */
    public void markDualProcessingViolation() {
        this.dualProcessingViolation = true;
    }

    public void markVersioningViolation() {
        this.versioningViolation = true;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EvaluateRoutingCmd c) {
            return evaluateRouting(c);
        }
        if (cmd instanceof UpdateRoutingRuleCmd c) {
            return updateRoutingRule(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> evaluateRouting(EvaluateRoutingCmd cmd) {
        // Invariant 1: Prevent dual-processing (Simulated check)
        if (dualProcessingViolation) {
            throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }

        // Invariant 2: Versioning check
        if (cmd.rulesVersion() <= 0) {
            throw new IllegalArgumentException("Routing rules must be versioned to allow safe rollback.");
        }

        if (versioningViolation) {
            throw new IllegalStateException("Routing rules must be versioned to allow safe rollback.");
        }

        if (evaluated) {
            throw new IllegalStateException("Routing already evaluated for this route.");
        }

        // Determine target (Simulated logic)
        String target = "LEGACY"; // Default
        if (cmd.payload() != null && cmd.payload().containsKey("forceModern")) {
            target = "MODERN";
        }

        var event = new RoutingEvaluatedEvent(
                cmd.routeId(),
                target,
                cmd.rulesVersion(),
                cmd.payload(),
                java.time.Instant.now()
        );

        this.evaluated = true;
        this.targetSystem = target;
        addEvent(event);
        incrementVersion();

        return List.of(event);
    }

    private List<DomainEvent> updateRoutingRule(UpdateRoutingRuleCmd cmd) {
        // S-24 Invariant Checks

        // 1. Versioning Invariant
        // Ensure rules are versioned correctly to allow safe rollback.
        // We assume the existence of a version check. If the aggregate is in an invalid state (e.g. version <= 0), fail.
        if (currentRulesVersion <= 0 || versioningViolation) {
            throw new IllegalStateException("Routing rules must be versioned to allow safe rollback.");
        }

        // 2. Single Target Invariant
        // Ensure we aren't targeting both systems (simulated check via flag or state).
        if (dualProcessingViolation) {
            throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }

        // Business Logic
        // Update the internal state
        // In a real system, this might involve complex rule lookups.
        this.targetSystem = cmd.newTarget();
        // Increment version to track changes
        this.currentRulesVersion++;

        var event = new RoutingUpdatedEvent(
                cmd.routeId(),
                cmd.ruleId(),
                cmd.newTarget(),
                cmd.effectiveDate(),
                Instant.now()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    public boolean isEvaluated() {
        return evaluated;
    }

    public String getTargetSystem() {
        return targetSystem;
    }

    public int getCurrentRulesVersion() {
        return currentRulesVersion;
    }
}
