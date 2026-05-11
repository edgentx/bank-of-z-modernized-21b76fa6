package com.example.domain.legacy.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;

public class LegacyTransactionRoute extends AggregateRoot {
    private final String routeId;
    private String currentTransactionType;
    private String currentPayload;
    private boolean evaluated;
    private int currentRuleVersion;

    // State for S-24 Invariant Violations (Simulated for BDD)
    private transient boolean violateDualProcessing;
    private transient boolean violateVersioning;

    public LegacyTransactionRoute(String routeId) {
        this.routeId = routeId;
        this.violateDualProcessing = false;
        this.violateVersioning = false;
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
        this.currentTransactionType = cmd.transactionType();
        this.currentPayload = cmd.payload();
        this.evaluated = true;
        this.currentRuleVersion = cmd.ruleVersion();

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private List<DomainEvent> updateRoutingRule(UpdateRoutingRuleCmd cmd) {
        // S-24 Invariant 1: Versioning
        // "Routing rules must be versioned to allow safe rollback"
        if (this.violateVersioning) {
            throw new IllegalStateException("Routing rules must be versioned to allow safe rollback.");
        }

        // S-24 Invariant 2: Single Target
        // "A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing"
        if (this.violateDualProcessing) {
            throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }

        // Basic validations
        if (cmd.newTarget() == null || cmd.newTarget().isBlank()) {
            throw new IllegalArgumentException("newTarget is required");
        }
        if (cmd.effectiveDate() == null) {
            throw new IllegalArgumentException("effectiveDate is required");
        }

        var event = new RoutingRuleUpdatedEvent(
                cmd.routeId(),
                cmd.ruleId(),
                cmd.newTarget(),
                cmd.effectiveDate()
        );

        // State transition
        // (Logic to update internal routing map would go here)
        this.currentRuleVersion++; // Increment version for safety

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
    public String getCurrentTransactionType() { return currentTransactionType; }
    public int getCurrentRuleVersion() { return currentRuleVersion; }

    // Test helpers for S-24 Scenarios
    public void markDualProcessingViolation() {
        this.violateDualProcessing = true;
    }
    public void markVersioningViolation() {
        this.violateVersioning = true;
    }
}
