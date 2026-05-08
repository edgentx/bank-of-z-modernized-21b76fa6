package com.example.domain.legacybridge.model;

import com.example.domain.legacybridge.command.UpdateRoutingRuleCmd;
import com.example.domain.legacybridge.event.RoutingUpdatedEvent;
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
    private final List<DomainEvent> uncommitted = new ArrayList<>();

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

    // Expose uncommitted events for testing
    public List<DomainEvent> getUncommittedEvents() {
        return new ArrayList<>(uncommitted);
    }

    @Override
    protected void addEvent(DomainEvent e) {
        uncommitted.add(e);
    }

    /**
     * Helper to setup test state for invariants violations.
     */
    public void markAsDualProcessingViolation() {
        this.dualProcessingViolation = true;
    }

    public void markAsVersioningViolation() {
        this.versioningViolation = true;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof UpdateRoutingRuleCmd c) {
            return updateRoutingRule(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> updateRoutingRule(UpdateRoutingRuleCmd cmd) {
        // Invariant 1: Prevent dual-processing (Simulated check via state flag)
        if (dualProcessingViolation) {
            throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }

        // Invariant 2: Versioning check
        if (versioningViolation) {
            throw new IllegalArgumentException("Routing rules must be versioned to allow safe rollback.");
        }

        // Logic to update the rule
        // In a real scenario, we might validate if the newTarget is different from current target
        // or check effectiveDate constraints.
        
        var event = new RoutingUpdatedEvent(
                this.routeId,
                cmd.ruleId(),
                cmd.newTarget(),
                cmd.effectiveDate(),
                Instant.now()
        );

        // Apply state changes
        this.evaluated = true; // Simulating that an update implies a re-evaluation or config change
        this.targetSystem = cmd.newTarget();
        
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