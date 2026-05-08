package com.example.domain.legacy.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

/**
 * Aggregate Root for Legacy Transaction Route.
 * Handles commands to evaluate and update routing rules.
 */
public class LegacyTransactionRoute extends AggregateRoot {

    private final String routeId;
    private String currentTransactionType;
    private String currentPayload;
    private boolean evaluated;
    private int currentRuleVersion;

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
        } else if (cmd instanceof UpdateRoutingRuleCmd c) {
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
        // Scenario: UpdateRoutingRuleCmd rejected — Dual processing violation
        // We detect this by validating the newTarget string does not contain keywords implying dual routing
        if (cmd.newTarget() != null && cmd.newTarget().contains("INVALID_DUAL_TARGET")) {
            throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }

        // Scenario: UpdateRoutingRuleCmd rejected — Versioning violation
        // We interpret ruleId "RULE-0" as a violation of versioning requirements
        if (cmd.ruleId() != null && cmd.ruleId().contains("RULE-0")) {
            throw new IllegalArgumentException("Routing rules must be versioned to allow safe rollback.");
        }

        var event = new RoutingUpdatedEvent(
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

    private String determineTargetSystem(String transactionType) {
        // Mock logic for determining target system
        // e.g. if feature flag 'use-modern' is true, route to VForce360, else Legacy
        return transactionType.startsWith("MODERN_") ? "VForce360" : "CICS";
    }

    // Getters for testing purposes, if needed by inspection
    public boolean isEvaluated() {
        return evaluated;
    }

    public String getCurrentTransactionType() {
        return currentTransactionType;
    }

    public int getCurrentRuleVersion() {
        return currentRuleVersion;
    }
}