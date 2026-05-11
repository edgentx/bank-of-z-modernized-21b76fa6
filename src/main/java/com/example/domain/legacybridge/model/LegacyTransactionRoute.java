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

    // State for the aggregate
    private int currentVersion;

    public LegacyTransactionRoute(String routeId) {
        this.routeId = routeId;
        this.dualProcessingViolation = false;
        this.versioningViolation = false;
        this.evaluated = false;
        this.currentVersion = 1;
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
        } else if (cmd instanceof UpdateRoutingRuleCmd c) {
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
        // Invariant: Check for dual processing violation state
        if (this.dualProcessingViolation) {
            throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }

        // Invariant: Check for versioning violation state
        if (this.versioningViolation) {
            throw new IllegalArgumentException("Routing rules must be versioned to allow safe rollback.");
        }

        // Input Validation
        if (cmd.ruleId() == null || cmd.ruleId().isBlank()) {
            throw new IllegalArgumentException("ruleId cannot be blank");
        }
        if (cmd.newTarget() == null || cmd.newTarget().isBlank()) {
            throw new IllegalArgumentException("newTarget cannot be blank");
        }
        if (cmd.effectiveDate() == null) {
            throw new IllegalArgumentException("effectiveDate cannot be null");
        }

        // Update state
        this.targetSystem = cmd.newTarget();
        this.currentVersion = cmd.newVersion();

        var event = new RoutingUpdatedEvent(
                cmd.routeId(),
                cmd.ruleId(),
                cmd.newTarget(),
                cmd.newVersion(),
                cmd.effectiveDate()
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

    public int getCurrentVersion() {
        return currentVersion;
    }
}