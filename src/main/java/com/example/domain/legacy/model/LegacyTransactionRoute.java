package com.example.domain.legacy.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;
import java.time.Instant;
import java.util.List;

/**
 * Aggregate root for Legacy Transaction Routing.
 * Handles the decision logic for routing transactions between Legacy (z/OS) and Modern systems.
 */
public class LegacyTransactionRoute extends AggregateRoot {

    private final String routeId;
    private String currentTransactionId;
    private String targetSystem;
    private int ruleVersion;
    private boolean routed;

    public LegacyTransactionRoute(String routeId) {
        this.routeId = routeId;
        this.ruleVersion = 1; // Default rule version
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
        if (cmd.targetSystem() == null || (!cmd.targetSystem().equals("LEGACY") && !cmd.targetSystem().equals("MODERN"))) {
            throw new IllegalArgumentException("Target system must be exactly one of LEGACY or MODERN");
        }

        // Invariant: Routing rules must be versioned (assuming version must be > 0)
        if (cmd.ruleVersion() <= 0) {
            throw new IllegalArgumentException("Routing rules must be versioned (version > 0)");
        }

        var event = new RoutingEvaluatedEvent(
            this.id(),
            cmd.transactionId(),
            cmd.transactionType(),
            cmd.targetSystem(),
            Instant.now()
        );

        this.currentTransactionId = cmd.transactionId();
        this.targetSystem = cmd.targetSystem();
        this.ruleVersion = cmd.ruleVersion();
        this.routed = true;

        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    public boolean isRouted() {
        return routed;
    }

    public String getTargetSystem() {
        return targetSystem;
    }
}
