package com.example.domain.legacybridge.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.util.List;

/**
 * Aggregate Root for Legacy Transaction Routing within the Legacy Bridge context.
 * This implementation adds the UpdateRoutingRuleCmd capability.
 */
public class LegacyTransactionRoute extends AggregateRoot {

    private final String routeId;
    // State fields derived from existing context/definitions
    private boolean dualProcessingViolation;
    private boolean versioningViolation;
    private boolean evaluated; // existing state
    private String targetSystem; // existing state
    
    // New State for UpdateRoutingRuleCmd
    private String currentRuleId;
    private int currentRuleVersion = 1;

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
     * Test helper to setup invariant violations for BDD scenarios.
     */
    public void markDualProcessingViolation() {
        this.dualProcessingViolation = true;
    }

    public void markVersioningViolation() {
        this.versioningViolation = true;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        // Existing routing evaluation logic
        if (cmd instanceof EvaluateRoutingCmd c) {
            return evaluateRouting(c);
        }
        // New Feature: Update Routing Rule
        if (cmd instanceof UpdateRoutingRuleCmd c) {
            return updateRoutingRule(c);
        }
        throw new UnknownCommandException(cmd);
    }

    /**
     * Handles the UpdateRoutingRuleCmd command.
     * Enforces business invariants regarding dual-processing and versioning.
     */
    private List<DomainEvent> updateRoutingRule(UpdateRoutingRuleCmd cmd) {
        // Invariant: Prevent dual-processing
        // Scenario 2: The aggregate state or command attributes prevent single-target routing
        if (this.dualProcessingViolation) {
            throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }
        
        // Check if the newTarget implies a configuration causing dual processing (mock logic)
        if ("DUAL".equalsIgnoreCase(cmd.newTarget())) {
             throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }

        // Invariant: Versioning check
        // Scenario 3: Rules must be versioned (positive integer)
        if (this.versioningViolation) {
            throw new IllegalArgumentException("Routing rules must be versioned to allow safe rollback.");
        }

        if (cmd.newRuleVersion() <= 0) {
            throw new IllegalArgumentException("Routing rules must be versioned to allow safe rollback.");
        }

        // Apply state changes
        this.currentRuleId = cmd.ruleId();
        this.currentRuleVersion = cmd.newRuleVersion();
        this.targetSystem = cmd.newTarget();
        
        var event = new RoutingUpdatedEvent(
            cmd.routeId(),
            cmd.ruleId(),
            cmd.newTarget(),
            cmd.newRuleVersion(),
            java.time.Instant.now()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    /**
     * Existing routing logic from prior stories (S-23, S-4) preserved.
     */
    private List<DomainEvent> evaluateRouting(EvaluateRoutingCmd cmd) {
        // Invariant 1: Prevent dual-processing
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

        // Determine target
        String target = "LEGACY";
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

    // Getters for Projections/Tests
    public boolean isEvaluated() { return evaluated; }
    public String getTargetSystem() { return targetSystem; }
    public String getCurrentRuleId() { return currentRuleId; }
    public int getCurrentRuleVersion() { return currentRuleVersion; }
}
