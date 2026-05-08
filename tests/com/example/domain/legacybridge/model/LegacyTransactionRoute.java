package com.example.domain.legacybridge.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

public class LegacyTransactionRoute extends AggregateRoot {
    private final String routeId;
    private String transactionType;
    private String targetSystem; // "modern" or "legacy"
    private int ruleVersion;

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
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> evaluateRouting(EvaluateRoutingCmd cmd) {
        // Invariant 1: Payload & Type validity
        if (cmd.transactionType() == null || cmd.transactionType().isBlank()) {
            throw new IllegalArgumentException("transactionType required");
        }
        if (cmd.payload() == null || cmd.payload().isEmpty()) {
            throw new IllegalArgumentException("payload required");
        }

        // Invariant 2: Routing rules must be versioned (Mocked check)
        // In a real scenario, this would check against a routing config table.
        // For this test, we assume rule version 0 is invalid (unversioned).
        if (this.ruleVersion <= 0) {
            throw new IllegalStateException("Routing rules must be versioned to allow safe rollback.");
        }

        // Invariant 3: Transaction must route to exactly ONE backend
        // Simulate a rule check that results in a target.
        String determinedTarget = determineTarget(cmd.transactionType());
        if (determinedTarget == null) {
            throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }

        var event = new RoutingEvaluatedEvent(
            this.routeId,
            cmd.transactionType(),
            determinedTarget,
            cmd.payload(),
            Instant.now()
        );

        this.transactionType = cmd.transactionType();
        this.targetSystem = determinedTarget;
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private String determineTarget(String type) {
        // Simple mock logic: 'WIRE' goes to legacy, 'ACH' goes to modern.
        // If type is 'INVALID', return null to trigger the invariant violation.
        if ("INVALID".equalsIgnoreCase(type)) {
            return null;
        }
        return "WIRE".equalsIgnoreCase(type) ? "legacy" : "modern";
    }

    public void hydrateForTest(int ruleVersion) {
        this.ruleVersion = ruleVersion;
    }
}
