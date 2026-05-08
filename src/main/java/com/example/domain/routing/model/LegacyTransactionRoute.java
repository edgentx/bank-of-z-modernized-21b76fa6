package com.example.domain.routing.model;

import com.example.domain.shared.AggregateRoot;
import com.example.domain.shared.Command;
import com.example.domain.shared.DomainEvent;
import com.example.domain.shared.UnknownCommandException;

import java.time.Instant;
import java.util.List;

public class LegacyTransactionRoute extends AggregateRoot {
    private final String routeId;
    private boolean evaluated = false;
    private int version = 0;

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
        if (evaluated) {
            throw new IllegalStateException("Routing already evaluated for this transaction");
        }
        
        // Scenario 2: A transaction must route to exactly one backend system
        if (cmd.targetSystem() == null || cmd.targetSystem().isBlank()) {
            throw new IllegalArgumentException("Target system must be explicitly defined (modern or legacy).");
        }

        // Scenario 3: Routing rules must be versioned
        if (cmd.ruleVersion() <= 0) {
            throw new IllegalArgumentException("Routing rules must be versioned to allow safe rollback.");
        }

        var event = new RoutingEvaluatedEvent(
            this.routeId,
            cmd.transactionType(),
            cmd.targetSystem(),
            cmd.payload(),
            cmd.ruleVersion(),
            Instant.now()
        );

        this.evaluated = true;
        addEvent(event);
        incrementVersion();
        return List.of(event);
    }

    @Override
    public int getVersion() {
        return version;
    }

    protected void incrementVersion() {
        this.version++;
    }
}