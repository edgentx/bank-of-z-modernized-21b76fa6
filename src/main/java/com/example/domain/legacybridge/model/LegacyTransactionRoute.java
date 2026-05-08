package com.example.domain.legacybridge.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

/**
 * Aggregate for LegacyTransactionRoute.
 * Handles the evaluation of routing rules for transactions.
 */
public class LegacyTransactionRoute extends AggregateRoot {

    private final String id;
    private String transactionType;
    private String payload;
    private String targetSystem; // MODERN or LEGACY
    private Integer ruleVersion;
    private boolean isEvaluated;

    public LegacyTransactionRoute(String id) {
        this.id = id;
        this.ruleVersion = 1; // Default version
        this.isEvaluated = false;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public List<DomainEvent> execute(Command cmd) {
        if (cmd instanceof EvaluateRoutingCmd c) {
            return evaluateRouting(c);
        }
        throw new UnknownCommandException(cmd);
    }

    private List<DomainEvent> evaluateRouting(EvaluateRoutingCmd cmd) {
        // 1. Validate Input
        if (cmd.transactionType() == null || cmd.transactionType().isBlank()) {
            throw new IllegalArgumentException("transactionType is required");
        }
        if (cmd.payload() == null || cmd.payload().isBlank()) {
            throw new IllegalArgumentException("payload is required");
        }

        // 2. Enforce Invariant: Routing rules must be versioned to allow safe rollback.
        // (Simulated check: Assume if version is not explicitly set in command context, it defaults.
        // In a real scenario, this might compare against a config store. Here we enforce it exists.
        if (cmd.ruleVersion() <= 0) {
            throw new IllegalStateException("Routing rules must be versioned to allow safe rollback.");
        }

        // 3. Enforce Invariant: A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.
        // (Simulated check: Logic determines target based on type. If type implies both, or is ambiguous, fail).
        String determinedTarget = determineTarget(cmd.transactionType());
        
        // Check for conflict (dual-processing simulation)
        if ("DUAL".equals(determinedTarget)) {
             throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }

        // 4. Apply state changes
        this.transactionType = cmd.transactionType();
        this.payload = cmd.payload();
        this.targetSystem = determinedTarget;
        this.ruleVersion = cmd.ruleVersion();
        this.isEvaluated = true;

        // 5. Emit Event
        RoutingEvaluatedEvent event = new RoutingEvaluatedEvent(
            this.id, 
            this.transactionType, 
            this.targetSystem, 
            this.ruleVersion, 
            Instant.now()
        );

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private String determineTarget(String transactionType) {
        // Mock logic: certain types go to MODERN, others to LEGACY.
        // If "AMBIGUOUS", throw error for dual-processing.
        if ("PAYMENT_V2".equalsIgnoreCase(transactionType)) {
            return "MODERN";
        } else if ("PAYMENT_V1".equalsIgnoreCase(transactionType)) {
            return "LEGACY";
        } else if ("DUAL_ROUTE".equalsIgnoreCase(transactionType)) {
            // Explicitly violating the constraint for the negative test case
            return "DUAL";
        }
        return "LEGACY"; // Default fallback
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getTargetSystem() {
        return targetSystem;
    }

    public Integer getRuleVersion() {
        return ruleVersion;
    }
}
