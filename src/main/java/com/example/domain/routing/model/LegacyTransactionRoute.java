package com.example.domain.routing.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.util.List;
import java.util.Map;

/**
 * Aggregate Root for LegacyTransactionRoute.
 * Manages the logic for determining whether a transaction should be routed
 * to the Modern platform or the Legacy mainframe system.
 */
public class LegacyTransactionRoute extends AggregateRoot {

    private final String routeId;
    private String transactionType;
    private String targetSystem;
    private int ruleVersion;

    // Enum for target systems to enforce strict typing in logic
    private enum SystemType { MODERN, LEGACY }

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
        // 1. Basic Input Validation
        if (cmd.transactionType() == null || cmd.transactionType().isBlank()) {
            throw new IllegalArgumentException("transactionType cannot be blank");
        }
        if (cmd.payload() == null) {
            throw new IllegalArgumentException("payload cannot be null");
        }

        // 2. Business Logic: Determine Target System
        // (In a real system, this would inspect Feature Flags and complex rules tables)
        String determinedTarget;
        int version = 1; // Default version

        if ("SWIFT_MT103".equals(cmd.transactionType())) {
            determinedTarget = "LEGACY";
        } else if ("DOMESTIC_WIRE".equals(cmd.transactionType())) {
            determinedTarget = "MODERN";
        } else {
            // Default fallback
            determinedTarget = "LEGACY";
        }

        // 3. Invariant Check: Exactly ONE system
        // (Logic enforcement - ensuring we didn't somehow derive a dual route)
        if (determinedTarget == null || determinedTarget.isBlank()) {
            throw new IllegalStateException(
                "A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing."
            );
        }

        // 4. Invariant Check: Versioning
        if (version <= 0) {
            throw new IllegalStateException(
                "Routing rules must be versioned to allow safe rollback."
            );
        }

        // 5. Apply State Changes
        this.transactionType = cmd.transactionType();
        this.targetSystem = determinedTarget;
        this.ruleVersion = version;

        // 6. Create Event
        var event = new RoutingEvaluatedEvent(
                this.routeId,
                this.transactionType,
                this.targetSystem,
                this.ruleVersion,
                cmd.payload(),
                java.time.Instant.now()
        );

        addEvent(event);
        incrementVersion();

        return List.of(event);
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getTargetSystem() {
        return targetSystem;
    }

    public int getRuleVersion() {
        return ruleVersion;
    }
}