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

    // Test state flags for invariants
    private transient boolean violateDualProcessing = false;
    private transient boolean violateVersioning = false;

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

    private List<DomainEvent> evaluateRouting(EvaluateRoutingCmd cmd) {
        // Invariant: Routing rules must be versioned (must be positive)
        if (cmd.ruleVersion() <= 0) {
            throw new IllegalArgumentException("Routing rules must be versioned to allow safe rollback.");
        }

        // Invariant: A transaction must route to exactly one backend system (no dual processing)
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
        // Invariant Check: Single Backend System
        if (violateDualProcessing) {
            throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }

        // Invariant Check: Versioning
        if (violateVersioning || cmd.ruleVersion() <= 0) {
            throw new IllegalStateException("Routing rules must be versioned to allow safe rollback.");
        }

        // Validation
        if (cmd.ruleId() == null || cmd.ruleId().isBlank()) {
            throw new IllegalArgumentException("ruleId cannot be blank");
        }
        if (cmd.newTarget() == null || cmd.newTarget().isBlank()) {
            throw new IllegalArgumentException("newTarget cannot be blank");
        }

        var event = new RoutingUpdatedEvent(
                cmd.routeId(),
                cmd.ruleId(),
                cmd.newTarget(),
                cmd.ruleVersion(),
                Instant.now()
        );

        // Apply state changes
        this.currentRuleVersion = cmd.ruleVersion();
        this.evaluated = true;
        this.currentPayload = "Updated Target: " + cmd.newTarget();

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private String determineTargetSystem(String transactionType) {
        return transactionType.startsWith("MODERN_") ? "VForce360" : "CICS";
    }

    public boolean isEvaluated() { return evaluated; }
    public String getCurrentTransactionType() { return currentTransactionType; }
    public int getCurrentRuleVersion() { return currentRuleVersion; }

    // Test helper methods
    public void setViolationDualProcessing(boolean violation) {
        this.violateDualProcessing = violation;
    }

    public void setViolationVersioning(boolean violation) {
        this.violateVersioning = violation;
    }
}
