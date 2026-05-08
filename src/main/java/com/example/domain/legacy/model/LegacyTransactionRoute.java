package com.example.domain.legacy.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

/**
 * Aggregate for managing LegacyTransactionRoute.
 * Determines the target system for an incoming command based on current feature flags and routing rules.
 */
public class LegacyTransactionRoute extends AggregateRoot {

    private final String routeId;
    private String transactionType;
    private String payload;
    private boolean evaluated;
    
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
        if (this.evaluated) {
            throw new IllegalStateException("Routing already evaluated for this route: " + routeId);
        }

        if (cmd.transactionType() == null || cmd.transactionType().isBlank()) {
            throw new IllegalArgumentException("transactionType is required");
        }
        if (cmd.payload() == null || cmd.payload().isBlank()) {
            throw new IllegalArgumentException("payload is required");
        }
        
        // Invariant: Routing rules must be versioned to allow safe rollback.
        // Simulating version check via command field.
        if (cmd.ruleVersion() == null || cmd.ruleVersion() <= 0) {
            throw new IllegalStateException("Routing rules must be versioned to allow safe rollback.");
        }

        // Invariant: A transaction must route to exactly one backend system (modern or legacy).
        if (cmd.isDualProcessingCandidate()) {
             throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }

        // Business Logic: Determine target
        String targetSystem = determineTargetSystem(cmd.transactionType());

        var event = new RoutingEvaluatedEvent(
            this.routeId,
            cmd.transactionType(),
            targetSystem,
            cmd.ruleVersion(),
            Instant.now()
        );

        this.transactionType = cmd.transactionType();
        this.payload = cmd.payload();
        this.evaluated = true;

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    private String determineTargetSystem(String txType) {
        // Simplified logic: Routing to 'Legacy' or 'Modern' based on type
        // This would normally involve feature flags.
        return switch (txType) {
            case "MODERN_TX" -> "Modern";
            default -> "Legacy";
        };
    }
}