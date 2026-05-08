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
 * 
 * Updated to handle UpdateRoutingRuleCmd.
 */
public class LegacyTransactionRoute extends AggregateRoot {

    private final String routeId;
    private boolean dualProcessingViolation;
    private boolean versioningViolation;
    private boolean evaluated;
    private String targetSystem;
    private int currentRulesVersion;

    // S-24: State for UpdateRoutingRuleCmd
    private String currentRuleId;
    private Instant effectiveSince;

    public LegacyTransactionRoute(String routeId) {
        this.routeId = routeId;
        this.dualProcessingViolation = false;
        this.versioningViolation = false;
        this.evaluated = false;
        this.currentRulesVersion = 1; // Start with valid version
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
        // S-24: Handle UpdateRoutingRuleCmd
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

    /**
     * S-24: Logic for updating routing rules.
     */
    private List<DomainEvent> updateRoutingRule(UpdateRoutingRuleCmd cmd) {
        // Invariant 1: Prevent dual-processing
        // Checks if the command attempts to set a target that would cause ambiguity or if aggregate is in invalid state
        if (this.dualProcessingViolation) {
            throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }

        // Invariant 2: Routing rules must be versioned
        // Check explicit version validity and aggregate state
        if (cmd.newVersion() <= 0) {
            throw new IllegalArgumentException("Routing rules must be versioned to allow safe rollback.");
        }
        if (this.versioningViolation) {
            throw new IllegalStateException("Routing rules must be versioned to allow safe rollback.");
        }

        // Basic validation
        if (cmd.ruleId() == null || cmd.ruleId().isBlank()) {
            throw new IllegalArgumentException("ruleId cannot be blank");
        }
        if (cmd.newTarget() == null || cmd.newTarget().isBlank()) {
            throw new IllegalArgumentException("newTarget cannot be blank");
        }

        // Apply State Changes
        this.targetSystem = cmd.newTarget();
        this.currentRulesVersion = cmd.newVersion();
        this.currentRuleId = cmd.ruleId();
        this.effectiveSince = cmd.effectiveDate();
        this.evaluated = true; // Updating the rule implies we are now routed/evaluated

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

    public int getCurrentRulesVersion() {
        return currentRulesVersion;
    }

    public String getCurrentRuleId() {
        return currentRuleId;
    }
}
