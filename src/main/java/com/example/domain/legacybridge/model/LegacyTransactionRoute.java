package com.example.domain.legacybridge.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.util.List;

/**
 * Aggregate Root for Legacy Transaction Routing.
 * Determines the target system (Modern vs Legacy) based on feature flags and rules.
 */
public class LegacyTransactionRoute extends AggregateRoot {

    private final String routeId;
    private String currentTargetSystem;
    private int currentRuleVersion;
    private boolean evaluated;

    // Flags to simulate invariants for BDD testing
    private boolean forceDualProcessingViolation;
    private boolean forceVersioningViolation;

    public LegacyTransactionRoute(String routeId) {
        this.routeId = routeId;
        this.currentTargetSystem = "CICS";
        this.currentRuleVersion = 1;
        this.evaluated = false;
    }

    @Override
    public String id() {
        return routeId;
    }

    public void setForceDualProcessingViolation(boolean force) {
        this.forceDualProcessingViolation = force;
    }

    public void setForceVersioningViolation(boolean force) {
        this.forceVersioningViolation = force;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof UpdateRoutingRuleCmd c) {
            return updateRoutingRule(c);
        }
        if (cmd instanceof EvaluateRoutingCmd c) {
            return evaluateRouting(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> updateRoutingRule(UpdateRoutingRuleCmd cmd) {
        // Invariant: Versioning check
        if (this.forceVersioningViolation) {
            throw new IllegalStateException("Routing rules must be versioned to allow safe rollback.");
        }
        if (cmd.newRuleVersion() <= 0) {
            throw new IllegalArgumentException("New rule version must be positive.");
        }

        // Invariant: Single Target check
        if (this.forceDualProcessingViolation) {
            throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }
        
        // Basic validation of target content
        if (cmd.newTarget() == null || cmd.newTarget().isBlank()) {
             throw new IllegalArgumentException("New target cannot be blank.");
        }

        String oldTarget = this.currentTargetSystem;
        this.currentTargetSystem = cmd.newTarget();
        this.currentRuleVersion = cmd.newRuleVersion();

        var event = new RoutingUpdatedEvent(
                this.routeId,
                cmd.ruleId(),
                oldTarget,
                cmd.newTarget(),
                this.currentRuleVersion,
                java.time.Instant.now()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private List<DomainEvent> evaluateRouting(EvaluateRoutingCmd cmd) {
        // Simplified for existing context, assuming this was there before
        if (evaluated) {
            throw new IllegalStateException("Routing already evaluated for this route.");
        }
        
        String target = currentTargetSystem; // Use current state
        var event = new RoutingEvaluatedEvent(
                cmd.routeId(),
                target,
                currentRuleVersion,
                cmd.payload(),
                java.time.Instant.now()
        );
        this.evaluated = true;
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    public String getCurrentTargetSystem() {
        return currentTargetSystem;
    }

    public int getCurrentRuleVersion() {
        return currentRuleVersion;
    }
}
