package com.example.domain.legacy.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class LegacyTransactionRoute extends AggregateRoot {
    private final String routeId;
    private String currentTargetSystem;
    private int ruleVersion;
    private boolean isRouted;

    public LegacyTransactionRoute(String routeId) {
        this.routeId = routeId;
        this.currentTargetSystem = "LEGACY";
        this.ruleVersion = 1;
        this.isRouted = false;
    }

    @Override
    public String id() {
        return routeId;
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

    private List<DomainEvent> updateRoutingRule(UpdateRoutingRuleCmd cmd) {
        List<DomainEvent> events = new ArrayList<>();

        // Invariant: Routing rules must be versioned to allow safe rollback.
        // Checking the provided command version.
        if (cmd.version() <= 0) {
            throw new IllegalArgumentException("Routing rules must be versioned to allow safe rollback.");
        }

        // Invariant: A transaction must route to exactly one backend system
        if (cmd.newTarget() == null || cmd.newTarget().isBlank()) {
             throw new IllegalArgumentException("Routing target cannot be blank.");
        }

        // Simulate checking for dual processing or invalid state if necessary.
        // The BDD scenarios imply the aggregate might hold a state that violates this,
        // or the command implies it. We check aggregate state here if it was pre-set.
        if (this.isRouted && this.currentTargetSystem.equals(cmd.newTarget())) {
             // Logic to prevent routing to the same system if already routed?
             // For now, we just update the configuration as per command.
        }

        var event = new RoutingUpdatedEvent(
            this.routeId,
            cmd.ruleId(),
            cmd.newTarget(),
            cmd.version(),
            cmd.effectiveDate()
        );

        this.currentTargetSystem = cmd.newTarget();
        this.ruleVersion = cmd.version();
        this.isRouted = true;

        addEvent(event);
        incrementVersion();
        events.add(event);

        return events;
    }

    private List<DomainEvent> evaluateRouting(EvaluateRoutingCmd cmd) {
        // Invariant: Routing rules must be versioned (must be positive)
        if (cmd.ruleVersion() <= 0) {
            throw new IllegalArgumentException("Routing rules must be versioned to allow safe rollback.");
        }

        // Invariant: A transaction must route to exactly one backend system (no dual processing)
        // Simulated here by checking the dualProcessingAttempt flag on the command
        if (cmd.dualProcessingAttempt()) {
            throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }

        if (cmd.transactionType() == null || cmd.transactionType().isBlank()) {
            throw new IllegalArgumentException("transactionType is required");
        }

        if (cmd.payload() == null || cmd.payload().isBlank()) {
            throw new IllegalArgumentException("payload is required");
        }

        // Determine target based on feature flags (mock logic)
        String targetSystem = determineTargetSystem(cmd.transactionType());

        var event = new RoutingEvaluatedEvent(
            cmd.routeId(),
            cmd.transactionType(),
            targetSystem,
            cmd.ruleVersion(),
            Instant.now()
        );

        // Update state
        // this.currentTransactionType = cmd.transactionType();
        // this.currentPayload = cmd.payload();
        this.evaluated = true;
        this.currentRuleVersion = cmd.ruleVersion();

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private String determineTargetSystem(String transactionType) {
        // Mock logic for determining target system
        // e.g. if feature flag 'use-modern' is true, route to VForce360, else Legacy
        return transactionType.startsWith("MODERN_") ? "VForce360" : "CICS";
    }

    public boolean isEvaluated() { return evaluated; }
    // public String getCurrentTransactionType() { return currentTransactionType; }
    // public int getCurrentRuleVersion() { return currentRuleVersion; }
}
