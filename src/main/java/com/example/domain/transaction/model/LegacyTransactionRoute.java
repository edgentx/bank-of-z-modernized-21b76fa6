package com.example.domain.transaction.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;
import java.util.Set;

public class LegacyTransactionRoute extends AggregateRoot {
    private final String routeId;
    private String currentTargetSystem; // "MODERN" or "LEGACY"
    private int routingRulesVersion;
    private boolean isRouted;

    public LegacyTransactionRoute(String routeId) {
        this.routeId = routeId;
        this.routingRulesVersion = 1;
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
        // Invariant: A transaction must route to exactly one backend system
        if (isRouted) {
            throw new IllegalStateException("Transaction has already been routed to " + currentTargetSystem + ". Dual-processing is not allowed.");
        }

        // Invariant: Routing rules must be versioned
        // Simulating a check that ensures we aren't using a "default" or unversioned rule set
        if (routingRulesVersion <= 0) {
            throw new IllegalStateException("Routing rules version must be positive to ensure safe rollback.");
        }

        // Logic to determine target system (Simplified for BDD)
        String target = "LEGACY"; // Default
        if (cmd.payload() != null && cmd.payload().containsKey("forceModern")) {
            target = "MODERN";
        }

        var event = new RoutingEvaluatedEvent(
                this.routeId,
                target,
                Set.of("RULE-" + routingRulesVersion),
                this.routingRulesVersion,
                Instant.now()
        );

        this.currentTargetSystem = target;
        this.isRouted = true;
        // incrementVersion(); // called by base logic if needed, or here
        
        addEvent(event);
        return List.of(event);
    }
    
    // Test utility setters
    public void setAlreadyRouted(String system) {
        this.isRouted = true;
        this.currentTargetSystem = system;
    }
    
    public void setInvalidRulesVersion(int version) {
        this.routingRulesVersion = version;
    }
}