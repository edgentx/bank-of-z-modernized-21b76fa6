package com.example.domain.legacy.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

/**
 * Aggregate for managing routing rules for legacy transactions.
 * Ensures transactions route to exactly one system and rules are versioned.
 */
public class LegacyTransactionRoute extends AggregateRoot {

    private final String routeId;
    private int ruleVersion;
    private String targetSystem; // "MODERN" or "LEGACY"
    private boolean dualProcessingViolation;

    public LegacyTransactionRoute(String routeId) {
        this.routeId = routeId;
        this.ruleVersion = 0;
        this.targetSystem = null;
        this.dualProcessingViolation = false;
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
        // Invariant: Routing rules must be versioned to allow safe rollback.
        // Assuming a new command implies a new version or update, we check state.
        // For this aggregate, we assume the command attempts to establish or update a route.
        
        // Check Dual Processing Invariant
        // If the command somehow implies routing to both, or the state is invalid.
        // Here we simulate a check based on command input or current state.
        if ("BOTH".equalsIgnoreCase(cmd.targetSystem())) {
             throw new IllegalArgumentException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }
        
        // Check Versioning Invariant
        // Rules must be versioned. If the command implies a non-positive version, reject.
        if (cmd.ruleVersion() <= 0) {
             throw new IllegalArgumentException("Routing rules must be versioned to allow safe rollback.");
        }

        // Apply state change
        this.targetSystem = cmd.targetSystem();
        this.ruleVersion = cmd.ruleVersion();
        
        var event = new RoutingEvaluatedEvent(
            this.routeId, 
            cmd.transactionType(), 
            this.targetSystem, 
            this.ruleVersion, 
            Instant.now()
        );
        
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    // Getters for testing/verification
    public String getTargetSystem() { return targetSystem; }
    public int getRuleVersion() { return ruleVersion; }
}
