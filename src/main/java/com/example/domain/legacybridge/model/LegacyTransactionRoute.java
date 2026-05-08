package com.example.domain.legacybridge.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

/**
 * Aggregate Root for Legacy Transaction Routing.
 * Handles the decision logic for routing incoming transactions to either the modernized platform
 * or the legacy mainframe system based on feature flags and business rules.
 */
public class LegacyTransactionRoute extends AggregateRoot {

    private final String routeId;
    private String currentRulesVersion;

    public LegacyTransactionRoute(String routeId) {
        this.routeId = routeId;
        // Default initialization if necessary
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
        if (cmd.targetRulesVersion() == null || cmd.targetRulesVersion().isBlank()) {
            throw new IllegalArgumentException("Routing rules must be versioned to allow safe rollback.");
        }

        // Determine Target System Logic
        String targetSystem;
        // Example Logic: If transaction type is 'INTERNATIONAL_WIRE', force Legacy for now
        if ("INTERNATIONAL_WIRE".equalsIgnoreCase(cmd.transactionType())) {
            targetSystem = "LEGACY";
        } else {
            targetSystem = "MODERN";
        }

        // Invariant: A transaction must route to exactly one backend system.
        if (targetSystem == null || (!"MODERN".equals(targetSystem) && !"LEGACY".equals(targetSystem))) {
            throw new IllegalStateException("A transaction must route to exactly one backend system (modern or legacy) to prevent dual-processing.");
        }

        // Create Event
        RoutingEvaluatedEvent event = new RoutingEvaluatedEvent(
                this.routeId,
                targetSystem,
                cmd.targetRulesVersion(),
                cmd.payload(),
                Instant.now()
        );

        // Apply state changes
        this.currentRulesVersion = cmd.targetRulesVersion();
        
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    public String getCurrentRulesVersion() {
        return currentRulesVersion;
    }
}
