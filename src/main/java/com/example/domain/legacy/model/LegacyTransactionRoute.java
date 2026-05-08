package com.example.domain.legacy.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

/**
 * Aggregate Root for Legacy Transaction Routing.
 * Handles determining the target system (modern vs legacy) based on feature flags and rules.
 */
public class LegacyTransactionRoute extends AggregateRoot {

    private final String routeId;
    private boolean dualProcessingViolation;
    private boolean versioningViolation;
    private Integer currentRuleVersion;

    public LegacyTransactionRoute(String routeId) {
        this.routeId = routeId;
        this.currentRuleVersion = 0; // Default state
        this.dualProcessingViolation = false;
        this.versioningViolation = false;
    }

    // --- Test/State Helpers ---
    
    // Use this in tests to simulate the aggregate already being in a bad state
    public void markAsDualProcessingViolation() {
        this.dualProcessingViolation = true;
    }

    // Use this in tests to simulate unversioned rules
    public void markAsVersioningViolation() {
        this.versioningViolation = true;
    }

    public String getRouteId() {
        return routeId;
    }

    // --- Command Handling ---

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EvaluateRoutingCmd c) {
            return evaluate(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> evaluate(EvaluateRoutingCmd cmd) {
        // 1. Invariant: A transaction must route to exactly one backend system
        if (dualProcessingViolation) {
            throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }

        // 2. Invariant: Routing rules must be versioned
        if (versioningViolation) {
            throw new IllegalStateException("Routing rules must be versioned to allow safe rollback.");
        }

        // 3. Business Logic
        // If the command provides a specific target, we validate it.
        // In a real scenario, this would involve looking up feature flags.
        String target = cmd.targetSystem();
        Integer version = cmd.ruleVersion();

        if (target == null || target.isBlank()) {
            throw new IllegalArgumentException("Target system cannot be empty");
        }

        // Apply state changes
        this.currentRuleVersion = version;

        // Create Event
        RoutingEvaluatedEvent event = new RoutingEvaluatedEvent(
                this.routeId,
                cmd.transactionType(),
                target,
                version,
                Instant.now()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }
}
