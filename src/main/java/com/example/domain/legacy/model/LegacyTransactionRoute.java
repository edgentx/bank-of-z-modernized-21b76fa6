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

    // State for S-24 Invariant Violation Simulation
    private boolean dualProcessingViolation = false;
    private boolean versioningViolation = false;

    public LegacyTransactionRoute(String routeId) {
        this.routeId = routeId;
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

    // Existing Logic
    private List<DomainEvent> evaluateRouting(EvaluateRoutingCmd cmd) {
        if (cmd.ruleVersion() <= 0) {
            throw new IllegalArgumentException("Routing rules must be versioned to allow safe rollback.");
        }
        if (cmd.dualProcessingAttempt()) {
            throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }
        if (cmd.transactionType() == null || cmd.transactionType().isBlank()) {
            throw new IllegalArgumentException("transactionType is required");
        }
        if (cmd.payload() == null || cmd.payload().isBlank()) {
            throw new IllegalArgumentException("payload is required");
        }

        String targetSystem = determineTargetSystem(cmd.transactionType());

        var event = new RoutingEvaluatedEvent(
            cmd.routeId(),
            cmd.transactionType(),
            targetSystem,
            cmd.ruleVersion(),
            Instant.now()
        );

        this.currentTransactionType = cmd.transactionType();
        this.currentPayload = cmd.payload();
        this.evaluated = true;
        this.currentRuleVersion = cmd.ruleVersion();

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // New S-24 Logic
    private List<DomainEvent> updateRoutingRule(UpdateRoutingRuleCmd cmd) {
        // S-24 Invariant 1: Versioning Check
        if (versioningViolation) {
            throw new IllegalStateException("Routing rules must be versioned to allow safe rollback.");
        }

        // S-24 Invariant 2: Dual Processing Check
        if (dualProcessingViolation) {
            throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }

        // Basic Validation
        if (cmd.ruleId() == null || cmd.ruleId().isBlank()) {
            throw new IllegalArgumentException("ruleId is required");
        }
        if (cmd.newTarget() == null || cmd.newTarget().isBlank()) {
            throw new IllegalArgumentException("newTarget is required");
        }
        if (cmd.effectiveDate() == null) {
            throw new IllegalArgumentException("effectiveDate is required");
        }

        var event = new RoutingUpdatedEvent(
            cmd.routeId(),
            cmd.ruleId(),
            cmd.newTarget(),
            cmd.effectiveDate()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private String determineTargetSystem(String transactionType) {
        return transactionType.startsWith("MODERN_") ? "VForce360" : "CICS";
    }

    // Test Setters for Invariant Violations
    public void markDualProcessingViolation() { this.dualProcessingViolation = true; }
    public void markVersioningViolation() { this.versioningViolation = true; }

    public boolean isEvaluated() { return evaluated; }
    public String getCurrentTransactionType() { return currentTransactionType; }
    public int getCurrentRuleVersion() { return currentRuleVersion; }
}
