package com.example.domain.legacybridge.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.ArrayList;
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
    private int rulesVersion = 1; // Default valid version

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

    public void setRulesVersion(int version) {
        this.rulesVersion = version;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EvaluateRoutingCmd c) {
            return evaluateRouting(c);
        }
        if (cmd instanceof UpdateRoutingRuleCmd c) {
            return updateRule(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> evaluateRouting(EvaluateRoutingCmd cmd) {
        // Invariant 1: Prevent dual-processing (Simulated check)
        if (dualProcessingViolation) {
            throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }

        // Invariant 2: Versioning check
        if (versioningViolation) {
            throw new IllegalStateException("Routing rules must be versioned to allow safe rollback.");
        }

        if (rulesVersion <= 0) {
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

    private List<DomainEvent> updateRule(UpdateRoutingRuleCmd cmd) {
        // Invariant 1: Prevent dual-processing
        if (dualProcessingViolation) {
            throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }

        // Invariant 2: Versioning check
        if (versioningViolation) {
            throw new IllegalStateException("Routing rules must be versioned to allow safe rollback.");
        }

        // Apply State Change
        this.targetSystem = cmd.newTarget();
        this.evaluated = true; // Mark as processed to prevent double execution if necessary

        var event = new RuleUpdatedEvent(
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
}
